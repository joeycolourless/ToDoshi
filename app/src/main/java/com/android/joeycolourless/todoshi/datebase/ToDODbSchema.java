package com.android.joeycolourless.todoshi.datebase;

/**
 * Created by admin on 19.03.2017.
 */

public class ToDODbSchema {
    public static final class ToDoTable{
        public static final String  NAME = "toDos";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DETAILS = "details";
            public static final String DATE = "date";
            public static final String DATE_CHANGE = "date_change";
            public static final String PRIORITY = "priority";
            public static final String POSITION = "position";
            public static final String NOTIFICATION_DATE = "notification_date";
            public static final String FINISH = "finish";
            public static final String NOTIFICATION = "notification";
            public static final String SYNC = "sync";
            public static final String IDFB = "id_firebase";
            public static final String SUCCESS = "success";
            public static final String SHOW_DATE_TEXT_VIEW = "show_date_text_view";
        }
    }

    public static final class ToDoCompletedTable{
        public static final String NAME = "toDosCompleted";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DETAILS = "details";
            public static final String DATE = "date";
            public static final String COMMENTS = "comments";
            public static final String FINISH = "finish";
            public static final String SUCCESS = "success";
            public static final String SYNC = "sync";
        }
    }

    public static final class ToDoDeletedTable{
        public static final String NAME = "deletedToDos";

        public static final class Cols{
            public static final String USER = "user";
            public static final String TABLE = "table";
        }
    }
}
