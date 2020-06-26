# Les Algorithmes


Au cours de notre travail, nous avons eu besoin d'utiliser trois algorithmes qui nous ont accompagnés tout au long de la progression du jeu : L'algorithme de mise à jour des données de jeu (nommé Update() ), l'algorithme de propagation d'eau et l'algorithme afin de vérifier la validité d'un niveau (vérifie que le niveau est finissable, on l'appelera "algorithme de validation de niveau"). 

**L'algorithme de mise à jour des données de jeu ( Update() ) :**

Cet algorithme commence par vider toutes les pièces du tableau de jeu et vérifie récursivement chacune des pièces en appelant une fonction auxiliaire "Update(x, y)". Update() commence par appeler Update(x, y) sur la première pièce du niveau et vérifie si :
- celle ci est pleine (si non, elle la remplit)
- si sa voisine *dans* le tableau est connectée à elle de par ses extrémités (si oui, Update (x, y) est appelée sur la/les pièces voisines connectées.

**L'algorithme de propagation d'eau ( Flow(i, j) )**

La fonction est appelée sur toutes les pièces remplie d'eau dans le tableau du modèle. 
Si la case correspondante dans le modèle est remplie, alors la propagation va commencer dans la Vue.
Si ce n'est pas le cas, elle se remplit d'abord elle même (elle est composé de 5 pièce en forme de croix) puis relance la propagation dans la Vue. 
À chaque fois qu'une pièce est remplie, elle est ajoutée à une pile. 
On appelle cette fonction à chaque rotation d'une pièce, et toutes les pièces qui ne sont pas reliées au début ou non remplis dans le jeu du niveau sont vidées et retirées de la pile. 
C'est la fonction isConnectedToSource() qui vérifie si elles sont reliées au début.
Pour chaque pièce de la pile, elle considère la dernière pièce tournée comme inaccessible et regarde si en appel recursif sur ses voisines connectées, elle peut rejoindre la source (donc sans passer par la dernière tournée).
Si c'est le cas, elle est donc connectée à la source.

**L'algorithme de validation de niveau :**

Cet algorithme n'a pas été utilisé dans la version finale du jeu car sa fiabilité n'était pas assez élevée pour qu'on en soit satisfait. 
Il nous a cependant permis de comprendre en détail le fonctionnement d'une création automatique de niveau, 
et nous a aidé à créer des niveaux finissables manuellement et similaires à ceux du jeu mobile. 
Il fonctionne comme Update(), il vérifie pour chaque rotation de la pièce si celle-ci peut être liée à une voisine et rappelle la fonction sur 
une copie du tableau (en utilisant la pièce voisine comme première) lorsque c'est le cas. 
La fonction retourne vrai uniquement lorsqu'elle est arrivée à la fin du niveau.