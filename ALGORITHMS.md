# Les Algorithmes


Au cours de notre travail, nous avons eu besoin d'utiliser trois algorithmes qui nous ont accompagné tout au long de la progression du jeu : L'algorithme de mise à jour des données de jeu (nommé Update() ), l'algorithme de propagation d'eau et l'algorithme afin de vérifier de la validité d'un niveau (vérifie que le niveau est finissage, on l'appeler l'algorithme de validation de niveau). 

**L'algorithme de mise à jour des données de jeu ( Update() ) : **

Cet algorithme commence par vider toutes les pièces du tableau de jeu et vérifie récursive ment chacune des pièces en appelant une fonction auxiliaire "Update(x, y)". Update() commence par appeler Update(x, y) sur la première pièce du niveau et vérifie si :
- celle ci est pleine (si non, elle la rempli)
- si sa voisine *dans* le tableau est connecté à elle de par ses extrémités (si oui, Update (x, y) est appelée sur la/les pièces voisines connectées.

**L'algorithme de propagation d'eau ( Flow(i, j) )**

La fonction est appelée sur toutes les pièces remplie d'eau dans le tableau du modèle. 
Si la case correspondante dans le modèle est remplie, alors la propagation va commencer dans la Vue. 
À chaque fois qu'une pièce est remplie, elle est ajoutée à une pile. 
On appelle cette fonction à chaque rotation d'une pièce, et toutes les pièces qui ne sont pas reliées au début du niveau sont vidées et retirées de la pile. 

**L'algorithme de validation de niveau : **

Cet algorithme n'a pas été utilisé dans la version finale du jeu car sa fiabilité n'était pas assez élevée pour qu'on en soit satisfait. 
Il nous a cependant permis de comprendre en détail le fonctionnement d'une création automatique de niveau, 
et nous à aider à créer des niveaux finissantes manuellement qui soient tout de même amusants pour le joueur. 
Il fonctionne comme Update(), il vérifie pour chaque rotation de la pièce si celle ci peut être liée à une voisine et rappelle la fonction sur 
une copie du tableau (en utilisant la pièce voisine comme première) lorsque c'est le cas. 
La fonction retourne vrai uniquement lorsqu'elle est arrivée à la fin du niveau.