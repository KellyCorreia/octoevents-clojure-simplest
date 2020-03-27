(ns octoevents-clojure.core
  (:use [clojure pprint])
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [octoevents-clojure.model :as o.model]
            [cheshire.core :as cheshire])
  (:gen-class))

(defn all-issues [req]
  "All events from all issues"
  {
   :status  200
   :headers {"Content-Type" "text/html"}
   :body    (o.model/pretty-event-collection @o.model/issueEventRepo)})

(defn issue-events [req]
  "Events by issue number"
  (let [issueNumber (:issueId (:params req))
         filteredIssueEvents (o.model/filterEventsBy issueNumber)]
     {:status  200
      :headers {"Content-Type" "text/html"}
      :body    (o.model/pretty-event-collection filteredIssueEvents)}))

(defn new-issue-event [req]
  "New issue handler"
  (let [request-body (slurp (:body req))
        request-body-map (cheshire/parse-string request-body true)
        issue-event (o.model/parseStringToEventIssue request-body-map)]
    (o.model/issueEventRepoAdd! issue-event)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (o.model/pretty-event-collection @o.model/issueEventRepo)}))

(defroutes app-routes
           (GET "/issues" [] all-issues)
           (GET "/issue" [] issue-events)
           (POST "/addEvent" [] new-issue-event)
           (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point. Which run the server with Ring.defaults middleware"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3001"))]
    (server/run-server (wrap-defaults #'app-routes api-defaults) {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
