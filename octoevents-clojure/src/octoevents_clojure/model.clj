(ns octoevents-clojure.model
  (:use [clojure pprint])
  (:require [cheshire.core :as cheshire]))

(def issueEventRepo (atom []))

(defn parse-int [value]
  (Integer/parseInt (re-find #"\A-?\d+" (str value))))

(defn eventIssueFactory [issueId, IssueUrl, issueTitle, created_at, action]
  {:issueId    (parse-int issueId),
   :issueUrl   IssueUrl,
   :issueTitle issueTitle,
   :created_at created_at,
   :action     action})

(defn parseStringToEventIssue [eventString]
  (let [issue (:issue eventString)]

    (eventIssueFactory (:id issue)
                       (:url issue)
                       (:title issue)
                       (:created_at issue)
                       (:action eventString))))

(defn issueEventRepoAdd [issueEventRepo issueEvent]
   (conj issueEventRepo issueEvent))

(defn issueEventRepoAdd! [issueEvent]
  (swap! issueEventRepo issueEventRepoAdd issueEvent))

(defn equalIssueId? [issueNumber issueEvent]
  (== issueNumber (:issueId issueEvent)))

(defn filterEventsBy [issueNumber]
  (filter #(equalIssueId? (parse-int issueNumber)  %) @issueEventRepo))

(defn pretty-event-item [event-item]
  (let [pretty-event-item {:action (:action event-item),
                           :created_at (:created_at event-item)
                           :issueId (:issueId event-item)}]
    (cheshire/generate-string pretty-event-item)))

(defn pretty-event-collection [event-collection]
  (map pretty-event-item event-collection))


;TODO - Move mocks and tests to test files
(defn newIssueEventMock []
  (eventIssueFactory "1254", "http://", "Title", "01/02/2020", "created"))

(defn newIssueEventMockB []
  (eventIssueFactory "1255", "http://", "Title", "01/02/2020", "created"))

;(defn testModel []
;  (let [issueEventMock (newIssueEventMock)
;        issueEventMockB (newIssueEventMockB)]
;    (issueEventRepoAdd! issueEventMock)
;    (issueEventRepoAdd! issueEventMockB)
;    (pprint (filterEventsBy (:issueId issueEventMockB)))))
;
;(testModel)

;(defn testEquals []
;  (let [issueA (newIssueEventMock)
;        issueB (newIssueEventMockB)]
;    (pprint(issueEquals? issueA issueB))))
;
;(testEquals)
