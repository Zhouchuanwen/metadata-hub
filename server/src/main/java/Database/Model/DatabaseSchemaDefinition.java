package Database.Model;

public class DatabaseSchemaDefinition {

    /**
     * Columns of the table "files"
     */
    public static final String FILES_TABLE = "files";

    public static final String FILES_ID = "id";
    public static final String FILES_CRAWL_ID = "crawl_id";
    public static final String FILES_DIR_PATH = "dir_path";
    public static final String FILES_NAME = "name";
    public static final String FILES_TYPE = "type";
    public static final String FILES_SIZE = "size";
    public static final String FILES_METADATA = "metadata";
    public static final String FILES_CREATION_TIME = "creation_time";
    public static final String FILES_ACCESS_TIME = "access_time";
    public static final String FILES_MODIFICATION_TIME = "modification_time";
    public static final String FILES_FILE_HASH = "file_hash";
    public static final String FILES_DELETED = "deleted";
    public static final String FILES_DELETED_TIME = "deleted_time";

    /**
     * Columns of the table "file_categories"
     */
    public static final String FILE_CATEGORIES_TABLE = "file_categories";

    public static final String FILE_CATEGORIES_FILE_CATEGORY = "file_category";
    public static final String FILE_CATEGORIES_FILE_TYPES = "file_types";

}
