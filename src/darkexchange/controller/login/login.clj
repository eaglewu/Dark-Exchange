(ns darkexchange.controller.login.login
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.login.create-user :as create-user]
            [darkexchange.controller.main.main-frame :as main-frame]
            [darkexchange.model.user :as user-model]
            [darkexchange.view.login.login :as login-view]
            [seesaw.core :as seesaw-core]))

(defn find-user-name-combobox [login-frame]
  (seesaw-core/select login-frame ["#user-name-combobox"]))

(defn reload-user-name-combobox [login-frame]
  (seesaw-core/config! (find-user-name-combobox login-frame) :model (user-model/all-user-names)))

(defn load-data [login-frame]
  (reload-user-name-combobox login-frame))

(defn attach-new-user-action [login-frame]
  (actions-utils/attach-listener login-frame "#new-user-button" (fn [_]  (create-user/show))))

(defn attach-cancel-action [login-frame]
  (actions-utils/attach-window-close-and-exit-listener login-frame "#cancel-button"))

(defn create-user-add-listener [login-frame]
  (fn [_]
    (reload-user-name-combobox login-frame)))

(defn attach-user-add-listener [login-frame]
  (user-model/add-user-add-listener (create-user-add-listener login-frame)))

(defn find-password-field [login-frame]
  (seesaw-core/select login-frame ["#password-field"]))

(defn password [login-frame]
  (.getPassword (find-password-field login-frame)))

(defn reset-password [login-frame]
  (.setText (find-password-field login-frame) ""))

(defn login-fail [login-frame]
  (seesaw-core/alert "You entered an invalid user name or password. Please reenter them and try again.")
  (reset-password login-frame))

(defn login-success [login-frame]
  (main-frame/show)
  (actions-utils/close-window login-frame))

(defn login [login-frame]
  (if-let [user-name (seesaw-core/selection (find-user-name-combobox login-frame))]
    (if (user-model/login user-name (password login-frame))
      (login-success login-frame)
      (login-fail login-frame))
    (seesaw-core/alert "You must select a user name. If no user exists, please create one.")))

(defn attach-login-action [login-frame]
  (actions-utils/attach-listener login-frame "#login-button" (fn [_]  (login login-frame))))

(defn attach [login-frame]
  (attach-user-add-listener login-frame)
  (attach-new-user-action login-frame)
  (attach-login-action login-frame)
  (attach-cancel-action login-frame))

(defn show []
  (let [login-frame (login-view/show)]
    (load-data login-frame)
    (attach login-frame)))