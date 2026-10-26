# TCL Home — bouton Home de la télécommande

Cette version ajoute le filtre d’intention Android HOME/DEFAULT à MainActivity. Cela rend TCL Home éligible au choix de l’application d’accueil sur les téléviseurs qui autorisent les launchers tiers.

## GitHub
Importer le contenu du ZIP à la racine du dépôt, conserver `.github/workflows/build.yml`, puis compiler `assembleDebug` avec GitHub Actions. Installer l’APK obtenu sur la TV.

## Définir l’accueil
Après installation, appuyer sur Home : si Android propose de choisir l’application d’accueil, sélectionner TCL Home puis « Toujours ». Sinon chercher « Application d’accueil » dans les paramètres des applications par défaut. Certains firmwares TCL forcent l’accueil constructeur et ne proposent pas ce choix.

## Vérification ADB facultative
Si ADB est disponible et autorisé, exécuter `adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME` pour inspecter l’accueil résolu. Sur les versions compatibles, `adb shell cmd package set-home-activity com.example.tcllauncher/.MainActivity` peut sélectionner TCL Home. Ne désactivez pas le launcher TCL d’origine. Le firmware peut ignorer ou refuser ces commandes.

## Retour arrière
Revenir au choix de l’application d’accueil et sélectionner le launcher TCL d’origine. Le bouton Quitter ne remplace pas le changement de launcher par défaut : Android peut relancer TCL Home dès que Home est demandé.

Le lancement automatique après démarrage complet et la prise en charge du bouton Home ne sont pas garantis sur tous les firmwares TCL. Ce projet n’a pas été compilé/testé sur le téléviseur réel.
