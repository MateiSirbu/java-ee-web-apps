package eu.msirbu.tw.tema1.server.constants;

/**
 * Enum that defines the possible statuses of CRUD operations on objects.
 */
public enum CRUDStatus {
    SUCCESS,                   // operation completed successfully
    FAIL_DUPLICATE,            // object is a duplicate; will not be used if duplicates are not a constraint
    FAIL_USER_NOT_FOUND,       // specified user is unknown; will not be used if operation is user-agnostic
    FAIL_USER_IS_DUPLICATE,    // user is duplicate; will not be used if operation is user-agnostic
    FAIL_EMPTY_ITEM,           // object specified is empty
    FAIL_ITEM_NOT_FOUND,       // object that should already be defined in a collection does not exist yet
    FAIL_MISCELLANEOUS_ERROR   // undefined error
}
