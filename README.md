# Android Etudiant Management App

## Description
Cette application Android permet de gérer une liste d'étudiants avec des fonctionnalités avancées telles que la suppression et la modification par balayage, la recherche et le tri par sexe. Elle utilise une interface simple avec un NavigationView pour naviguer entre les différentes sections de l'application.

## Fonctionnalités
- *Liste des étudiants* : Affiche tous les étudiants inscrits dans l'application.
- *Ajout d'un étudiant* : Permet d'ajouter un nouvel étudiant avec des informations telles que le nom, le prénom, etc.
- *Suppression/Modification par balayage* : Les étudiants peuvent être supprimés ou modifiés en effectuant un balayage (swipe) sur l'élément de la liste.
- *Recherche* : Permet de rechercher un étudiant par nom ou prénom via une barre de recherche.
- *Tri par sexe* : Permet de trier la liste des étudiants en fonction de leur sexe (homme/femme).
- *Menu de navigation* : Accès facile à toutes les sections de l'application via un menu latéral.

## Structure du Projet

Le projet est structuré de manière à séparer les différentes fonctionnalités dans des activités distinctes, et le menu de navigation permet de basculer entre ces activités.

- *Activities* :
  - MainActivity : L'activité principale où l'utilisateur interagit avec l'application et Permet l'ajout d'un nouvel étudiant.
  - ListEtudiantActivity : Affiche la liste des étudiants avec des options de recherche, tri, suppression et modification par balayage.
  - AddEtudiantActivity : Permet l'ajout d'un nouvel étudiant.
  
- *Layouts* :
  - activity_main.xml : Layout principal contenant un DrawerLayout avec un NavigationView.
  - activity_list_etudiant.xml : Layout affichant la liste des étudiants avec barre de recherche et tri.
  - activity_add_etudiant.xml : Layout pour ajouter un étudiant.
  - nav_header.xml : Layout de l'en-tête dans le menu de navigation.

- *Menu* :
  - menu.xml : Fichier de menu XML utilisé dans le NavigationView.

## Prérequis

- Android Studio
- SDK Android 21 ou supérieur
- Connexion Internet (pour les permissions de réseau)

## Permissions Utilisées

L'application utilise les permissions suivantes :

- *INTERNET* : Pour des fonctionnalités futures comme la synchronisation des données sur un serveur distant.
- *READ_EXTERNAL_STORAGE* et *WRITE_EXTERNAL_STORAGE* : Pour lire et écrire des données depuis/vers le stockage externe.
- *CAMERA* : Pour des fonctionnalités potentielles d'ajout de photo de profil des étudiants.

## Fonctionnalités Avancées

### Suppression et Modification par Balayage

- *Suppression* : Effectuer un balayage vers la gauche ou la droite sur un élément de la liste pour afficher une option de suppression.
- *Modification* : Effectuer un balayage pour modifier les informations de l'étudiant, telles que le nom ou les autres détails.

### Recherche

- Une barre de recherche située en haut de la liste des étudiants permet de filtrer les résultats en fonction du nom ou du prénom des étudiants.

### Tri par Sexe

- Un bouton ou un menu déroulant permet de trier les étudiants en fonction de leur sexe (homme/femme) pour une meilleure organisation.

## Installation

1. Clonez ce dépôt :
   ```bash
   git clone https://github.com/zinebtaghti/TP_ETUDIANTS
2.Ouvrez le projet dans Android Studio.

3.Synchronisez le projet avec Gradle et compilez l'application.

4.Exécutez l'application sur un émulateur ou un appareil physique.

## Video Descriptif


https://github.com/user-attachments/assets/fbfa94bd-a45e-461e-b239-0cb4e7800b38

