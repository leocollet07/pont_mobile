package pont_mobile;

public enum Etat {
	FERMÉ, // le pont est fermé à la circulation des bateaux et peut accueillir les véhicules
	OUVERTURE, // le pont est en cours d’ouverture. Aucun nouveau véhicule terrestre ne peut entrer sur le pont.
	OUVERT, // le pont est ouvert pour permettre le passage des bateaux.
	FERMETURE //  le pont est en cours de fermeture. Aucun véhicule terrestre ne peut entrer avant le retour à l’état FERMÉ.
}
