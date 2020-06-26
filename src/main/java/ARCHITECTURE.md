# Les Algorithmes


Le code est organisé en plusieurs catégories contenant, pour certaines, des sous catégories. 

Tout d'abord les pièces.

Les pièces sont regroupées autour d'une classe abstraite "Piece" qui nous a servi de "schéma" pour ce que doit être une pièce quelle que soit sa forme. Elle dispose de 4 face (haut, bas, gauche, droite), peut être pleine ou non. Elles sont également dotées d'un "index" pour indiquer quel est leur degré de rotation.

De cette classe abstraite, vient ensuite chacune des pièces dans leur individualité, à savoir PieceI, PieceL et PieceT. Ses pièces ont simplement des sorties définies en fonction de leur forme.

Après les pièces, chacun des niveau est un objet indépendant, faisant tourner sa propre partie dès qu'il est lancé. C'est pourquoi on y trouve toutes les méthodes de victoire/défaites, de vérifications des compteurs etc.

Enfin, une classe InputsWindow permettait de jouer au jeu en utilisant les boutons du clavier pour faire bouger un "curseur" dans le terminal. Tout ceci était en fait activé par une minuscule fenêtre invisible qui, à chaque touche du clavier pressé, envoyait une information au jeu et actualisait le terminal avec de nouvelles informations. 

Ces "trois" (en réalité six) classes constituaient à elles seules la partie textuelle du jeu, le modèle si l'on veut. Pour entrer dans l'aspect graphique du jeu, on va naturellement se diriger vers la vue, "View".

"View" contient la majeure partie d'une... partie ! Elle est responsable de l'affichage 3D des pièces mais également de la possibilité de cliquer sur les pièces, les menus "inGame" (pause et celui de fin de niveau). 

Une classe Clock a été ajouté en fin de parcours afin de pouvoir gérer les niveaux fonctionnant avec un compte à rebours. 

Une classe interne a été intégrée à View et c'est LevelEnd, qui nous permet d'afficher une petite carte dès qu'un niveau est terminé. La carte diffère en fonction du résultat de la partie : il nous est impossible de passer au niveau suivant en cas de défaite. Il faut persévérer !

Nous avons également ajouté une classe abstraite pour les pièces remplies, waterPiece, qui était finalement déclinée de la même façon que son homologue déshydraté. Elle gérait cependant une partie de l'algorithme de propagation de l'eau. Dès qu'une pièce se remplissait, elle devenait une waterPiece.

Le menu principal du jeu est décliné en plusieurs classes. D'abord la classe "Menu" qui ne servait en réalité que pour la partie textuelle du jeu a été délaissé au profit d'une classe bien plus riche en contenu. 

La classe MenuTitle ne prend en charge que le titre qui s'affiche dans la partie supérieure du menu principal. Il était à l'origine censé être de 2 couleurs différentes mais la tâche fut plus rude que prévue ! 

Ensuite, MenuItems gère les différentes cases sur lesquels le joueur peut cliquer pour accéder aux diverses parties du menu. 
LevelItems est la classe permettant d'afficher les différents niveaux dans l'onglet "Choix du niveau".
SettingsMenu est la classe permettant de regrouper les quelques réglages du jeu.
Enfin, MenuApplication est la classe qui regroupe toutes ces petites classes afin d'obtenir un menu rempli d'animations, de sons et de mouvements fluides pour accueillir le joueur de la meilleure façon possible. 
La classe AudioController, comme son nom l'indique, permettait de contrôler les nombreux sons qui jouaient simultanément durant une partie. 
Une classe levelTracker permet de garder en mémoire l'état d'avancement du jeu et du joueur, afin qu'il puisse garder sa progression lorsqu'il quitte le jeu. 
Et finalement, la classe cheatHandler gère les codes secrets implémentés dans le jeu permettant de sauter les niveaux lorsque ceux ci sont trop durs ! Mais bon, c'est déconseillé...

 
