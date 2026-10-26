# Compilation de TCL Home avec GitHub Actions

1. Crée un dépôt GitHub nommé `TCL-Home` (privé ou public).
2. Décompresse le ZIP et **importe son contenu à la racine du dépôt** : `app/`, `.github/`, `settings.gradle`, etc. (Ne conserve pas un dossier parent `TCL_Launcher/` dans le dépôt.)
3. Ouvre l’onglet **Actions** du dépôt. Si GitHub le demande, active les workflows.
4. Sélectionne **Build TCL Home APK** puis **Run workflow**. Un push sur `main` déclenche également la compilation.
5. À la fin du workflow, ouvre l’exécution réussie et télécharge l’artifact **TCL-Home-APK**.
6. Décompresse l’artifact pour obtenir `app-debug.apk`, puis installe-le sur la TV.

**Remarques :** Le workflow compile un APK de test non destiné au Play Store. Le build n’a pas été exécuté ici : en cas d’erreur, consulte les logs Actions. L’application ne se lance pas automatiquement au démarrage de la TV. Le bouton HDMI dépend des fonctions accessibles sur le firmware TCL. Les icônes des apps sont lues depuis les applications installées sur la TV.
