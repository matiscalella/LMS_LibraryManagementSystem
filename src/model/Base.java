package model;
/**
 * Base entity class for the Library Management System.
 * Provides the common fields 'id' and 'deleted' used by all domain entities.
 * Designed to support logical deletion and consistent identification across the system.
 */
public abstract class Base {
    private Long id;
    private boolean deleted;

    public Base(Long id, boolean deleted) {
        this.id = id;
        this.deleted = deleted;
    }

    public Base() {}
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
}
