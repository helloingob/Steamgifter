package com.helloingob.gifter.to;

public class AlgorithmTO {
    private Integer pk;
    private String name;
    private String description;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("AlgorithmTO [pk=%s, name=%s]", pk, name);
    }
}
