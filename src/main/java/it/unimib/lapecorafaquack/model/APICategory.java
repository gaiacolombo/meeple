package it.unimib.lapecorafaquack.model;

// classe creata per poter salvare la coppia "id" e "categoria" che ricevo dall'API
public class APICategory {
    private String id;

    public APICategory(){};

    public APICategory(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "APICategory{" +
                "id='" + id + '\'' +
                '}';
    }
}
