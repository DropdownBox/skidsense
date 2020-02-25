package me.skidsense.management.notifications;

public interface INotification {
   String getMessage();

   long getInitializeTime();

   long getDisplayTime();
}
