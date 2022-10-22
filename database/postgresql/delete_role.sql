/*
 * Sequence to drop a user/role
 */
REASSIGN OWNED BY the_user TO postgres;  -- or some other trusted role
DROP OWNED BY the_user;
DROP USER the_user;
