# TCL Home — projet Android

Prototype de launcher Android TV en Kotlin, conçu pour une navigation à la télécommande.

## Fonctions
- Horloge numérique en haut à droite
- Bouton Réglages et bouton Quitter
- Raccourcis YouTube, Netflix, Prime Video et Disney+
- Bouton Source HDMI (ouvre un écran système disponible en solution de repli ; l’accès direct aux entrées HDMI dépend du firmware TCL)
- Fonds de couleur, taille de texte et quatre thèmes d’horloge

## Construire l’application
1. Installer Android Studio et son JDK recommandé.
2. Ouvrir le dossier `TCL_Launcher` dans Android Studio.
3. Laisser Gradle synchroniser le projet et installer les composants SDK demandés.
4. Choisir Build > Build APK(s).
5. Installer l’APK sur la TV (par exemple via une clé USB ou `adb install`).

## Limites importantes
- Les noms de paquets des services de streaming peuvent varier selon la version et la région. Si une app n’est pas trouvée, le launcher affiche un message.
- Les firmwares TCL ne proposent pas tous une API publique pour basculer directement sur une entrée HDMI. Le bouton HDMI tente d’ouvrir une activité système ; si elle n’est pas prise en charge, utiliser la touche Source de la télécommande.
- L’application ne remplace pas automatiquement l’accueil système. La définir comme launcher par défaut et lancer automatiquement au démarrage dépend des possibilités de la version Android TV et du firmware. Ne désactivez pas l’accueil TCL sans avoir testé une méthode de retour.

## Fonds d’écran
Six fonds illustrés intégrés (Nuit, Océan, Forêt, Galaxie, Élégance, Montagne), sélectionnables dans ⚙ Réglages > Fonds d’écran. Le choix est mémorisé.
