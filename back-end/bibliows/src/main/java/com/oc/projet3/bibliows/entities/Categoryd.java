package com.oc.projet3.bibliows.entities;

public enum Categoryd {
    ROMAN ("Roman"),
    ART_CULTURE ("Art & Culture"),
    BD ("Bandes dessinées"),
    ENSEIGNEMENT_EDUCATION ("Bandes dessinées"),
    SANTE_BIEN_ETRE ("Bandes dessinées"),
    HISTOIRE_GEOGRAPHIE ("Bandes dessinées"),
    JEUNESSE ("Bandes dessinées"),
    LANGUES ("Bandes dessinées");

    Categoryd(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    private String libelle;
}
