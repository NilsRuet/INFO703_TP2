# TP Compilation : Génération de code

L'objectif du TP est de générer du code pour la machine à registres décrite dans le cours, afin d'être en mesure d'exécuter les programmes reconnus par l'analyseur sur la machine à registres.

## Partie 1

Dans cette première partie, on veut générer du code pour les expressions arithmétiques sur les nombres entiers. On va ainsi générer du code pour les opérations binaires (+ - * / mod), le moins unaire, les entiers constants et l'affectation et lecture d'identifiants. On va également générer du code pour les instructions in et out, afin de pouvoir débugguer le programme.

### Analyse des identifiants

Une première étape pour pouvoir transformer un arbre abstrait en code est de lister les identifiants utilisées dans le code. Ainsi, on va pouvoir construire la partie donnée du code assembleur généré.  
Pour cette étape, on va donc utiliser une structure de données qui garde en mémoire les identifiants utilisés. L'idée principale est de parcourir un arbre en listant les identifiants et en enlevant les doublons. 

Ainsi, avec le code 

```
let prixHt = 200;
let proportionTaxe = 119 / 100;
let prixTtc =  prixHt * proportionTaxe.
```

On isole les identifiants prixHt, proportionTaxe et prixTtc afin de générer la partie données du programme :


```
DATA SEGMENT
	prixHt DD
    proportionTaxe DD
	prixTtc DD
DATA ENDS
```

Une première optimisation qui peut être apportée à cette étape de compilation est de repérer les utilisations en lecture de variables non affectées. Pour faire ceci, on transforme légèrement la structure de données basique afin de garder en mémoire une pile de collections d'identifiants.  
A un étage donné de cette pile, les identifiants utilisables sont donc les identifiants de cet étage ou d'étages empilés précédemment. On va empiler une nouvelle collection d'identifiants chaque fois qu'on entre dans un nouveau contexte dans le code, c'est à dire lors de conditions ou de boucles. On dépile ensuite cette collection lorsque tout le code associé à la boucle/condition a été exécuté.  
Ainsi on peut détecter lors de l'utilisation d'un identifiant en lecture s'il a été correctement déclaré auparavant.

### Génération du code pour les opérations arithmétiques

Une fois les identifiants listés, il est possible de générer du code pour les expressions arithmétiques. Le principe de base des expressions arithmétiques est qu'une expression peut être une combinaison d'autres expressions ayant chacune une valeur. Afin de gérer la valeur de retour d'une expression, qui peut être utiliser par une instruction dans le code, une solution est de pousser sur la pile d'exécution la valeur de l'expression une fois qu'elle a été calculée. On part ainsi du principe que lorsqu'une expression doit être calculée, les valeurs de ces opérandes ont déjà été calculées, et ont été empilées sur la pile d'exécution.  
Pour calculer la valeur d'une expression, il suffit alors de dépiler les valeurs et d'appliquer l'opération adéquate.

Cette vision des choses est récursive, puisqu'on part du principe que les opérandes sont déjà calculées lorsque le compilateur doit effectuer une opération. On a donc besoin de générer du code pour certaines valeur de base, qui ne sont pas des opérations. Dans le cadre de ce TP, les valeurs de base peuvent être des entiers constants ou des identifiants.  
Puisque la valeur des opérandes doivent être empilées, on peut déduire la génération de code de la lecture d'un identifiant et de l'utilisation d'un entier.

Une partie du code lors du calcul d'une opération va donc être le chargement des opérandes. Un exemple d'opération est :
```
a + 5
```
Le chargement des opérandes serait donc compilé en
```
mov eax,a
push eax
mov eax,5
push eax
```

Puisque le compilateur peut maintenant charger les opérandes des expressions, alors on peut définir le code des opérations binaires et unaires. La plupart de ces opérations possèdent un équivalent direct en assembleur, et le principe utilisé par le compilateur sera donc le même.
Ainsi, le code d'une opération binaire
```
a + 5
```
peut être compilé en :
```
mov eax,a
push eax
mov eax,5
push eax
pop ebx
pop eax
add eax, ebx
push eax
```

L'utilisation de l'instruction pop permet de récupérer les valeurs des opérandes, il suffit ensuite d'appliquer l'instruction correspondante de la machine de turing, et d'empiler le résultat.  
Le même principe est appliqué pour les opérations unaires, à la différence qu'elles ne disposent que d'une opérande.

Deux opérations aithmétiques de lambada n'ont pas d'équivalent direct dans la machine à registres. Ce sont les opérateurs modulo et moins unaire.  
Pour compiler le moins unaire, on multiplie simplement l'opérande par -1.  
Pour compiler le modulo de a par b, on va réaliser le calcul a-((a/b)*b).
Ainsi le code 
```
5 % 2
```

est compilé en :

```
mov eax,5
push eax
mov eax,2
push eax ; les lignes ci-dessus chargent les opérandes
pop ebx ; à partir d'ici on réalise l'opération modulo
pop eax
mov ecx, eax
div eax, ebx
mul eax, ebx
sub ecx, eax
push ecx
```

### Génération du code pour certaines opérations de structuration du code

le compilateur peut maintenant générer du code qui calcule la valeur d'opérations arithmétiques. Cependant, pour avoir un prototype de compilateur testable, il reste 4 instructions à pouvoir compiler : let, input, output, et l'opérateur de séquentialisation (;).

La séquentialisation est très simple à réaliser, puisqu'elle séparer deux instructions. Il suffit alors de concaténer les codes générés par ces deux instructions pour retranscrire la notion de séquentialité.  

L'opérateur input fonctionne de la même manière qu'un entier ou un identifiant, puisqu'il permet de lire une valeur sur l'entrée standard. On utilise donc simplement l'instruction assembleur in et on empile la valeur récupérée.  
L'instruction out est similaire à un opérateur unaire, puisque c'est une instruction utilisant une seule opérande. Cependant, cette instruction ne renvoie pas de valeur, il n'y a donc pas besoin d'empiler de valeur une fois exécutée.  

La dernière instruction manquante pour réaliser un petit exemple est l'affectation avec let. Cette instruction utilise deux opérandes, la variable à affecter et la nouvelle valeur de cette variable. On sait que la variable à affecter n'est pas une valeur issue d'une expression, donc il n'y a pas besoin d'empiler sa valeur lors de la compilation. La valeur qui est affectée est cependant issue d'une expression, donc elle nécessite d'être dépilée avant l'affectation.

Un exemple d'affectation de variable est
```
let x = 3 .
```
qui serait compilé en
```
DATA SEGMENT
a DD
DATA ENDS
CODE SEGMENT
mov eax,3
push eax
pop eax
add eax, ebx
mov x,eax
CODE ENDS
```

### Optimisations

Dans l'état actuel, beaucoup d'opérations sont redondantes. En effet, chaque valeur d'une expression est empilée puis dépilée alors que cela n'est pas toujours nécessaire. Pour éviter ces opérations redondantes, une solution est de s'intéresser aux types des noeuds fils dans l'arbre abstrait au moment de compiler l'opération. Par exemple, si un noeud est de type entier, alors la valeur du noeud peut être utilisée directement comme opérande au lieu d'être empilée puis dépilée.

Un exemple d'implémentations de cette optimisation est le chargement des opérandes des opérateurs binaires. Lorsque la valeur d'une opération binaire est calculée, on veut initialement avoir chargé la valeur des opérandes dans des registres de la machine. En partant du principe que ces opérandes sont toujours chargées dans les mêmes registres avant une opération binaire, on peut optimiser le code en fonction du type des noeuds fils au noeud qui contient l'opération. Par exemple, si les opérandes sont toutes les deux des entiers, alors il suffit juste de charger leur valeur de eax et ebx pour éviter de devoir empiler puis dépiler ces valeurs.  
De même, si une seule des opérandes est un entier, alors on n'a besoin de calculer et empiler la valeur que de l'autre opérande uniquement.

Par exemple, le programme 
```
let a = 3 + 5 .
```

Initialement compilé en
```
DATA SEGMENT
a DD
DATA ENDS
CODE SEGMENT
mov eax,3
push eax
mov eax,5
push eax
pop ebx
pop eax
add eax, ebx
push eax
pop eax
mov a,eax
CODE ENDS
```

Est optimisé en
```
DATA SEGMENT
a DD
DATA ENDS
CODE SEGMENT
mov eax,3
mov ebx,5
add eax, ebx
push eax
pop eax
mov a,eax
CODE ENDS
```

### Exemple complet

Avec tous ces opérateurs, on peut réaliser un petit exemple qui permet de saisir et calculer des valeurs, et qui montre la gestion de la valeur des expressions composées.
```
let a = input ;
let b = input ;
let c = ((a + b) / 2) * 2;
output c .
```

```
DATA SEGMENT
a DD
b DD
c DD
DATA ENDS
CODE SEGMENT
in eax      ; Affectation de a
push eax
pop eax
mov a,eax
in eax      ; Affectation de b
push eax
pop eax
mov b,eax
mov eax,a   ; Calcul de la valeur à droite de l'affectation de c
mov ebx,b
add eax, ebx
push eax
pop eax
mov ebx,2
div eax, ebx
push eax
pop eax
mov ebx,2
mul eax, ebx
push eax
pop eax     ; Affectation à c
mov c,eax
mov eax,c   ; Affichage de c
push eax
pop eax
out eax
CODE ENDS
```

## Partie 2

L'objectif de cette partie est d'étendre la génération de code aux opérateurs booléens, de comparaison, aux boucles et aux conditionnelles.

Une première question qui se pose est la manière dont les booléens vont être représentés. Ici, le choix réalisé est de prendre la valeur 0 comme "faux" et toutes les autres valeurs comme "vrai". Ainsi la condition "5>3" se verra attribuer la valeur 1, et la condition "3>5" se verra attribuer la valeur 0.

### Opérateurs booléens

Ici, il y a une similitude entre la manière de gérer des opérations arithmétiques et des opérations booléennes. En effet, comme pour les expressions arithmétiques, une expression booléenne peut être une combinaison d'autres expressions booléennes. On va donc maintenir le même principe que celui utilisé pour les opérations arithmétiques, où les valeurs des opérandes sont empilées sur la pile d'exécution.

Les opérateurs booléens les plus élémentaires sont ici <, <= et =. Un problème commun pour compiler ces opérateurs est de renvoyer soit la valeur 0, soit la valeur 1, sans dépendre des opérandes. Pour renvoyer à coup sûr ses valeurs, il est possible d'utiliser des instructions de sauts conditionnels afin d'exécuter des sections du code qui empilent soit 0, soit 1.  
Chacun de ses opérateurs va donc utiliser les registres ZF et LT afin de décider du résultat à envoyer. Pour ces opérateurs, on va effectuer la différence entre les deux opérandes, et on peut ainsi déduire des registres ZF et LT laquelle des opérandes était la plus grande.
Pour pouvoir effectuer des sauts dans le code, il est nécessaire de marquer des endroits du code vers lesquels des sauts peuvent être effectués. Pour éviter les problèmes de marqueurs ayant des noms identiques, on utilise un compteur dont la valeur est ajoutée en fin du nom des marqueurs d'un opérateur booléen lorsqu'il est compilé. Ce compteur est incrémenté après utilisation. 

Ainsi, le code
```
a < 3
```
est compilé en
```
pop ebx
pop eax
sub eax, ebx
jge lt_false_1
push 1
jmp lt_end_1
lt_false_1:
push 0
lt_end_1:
```

Le deuxième type d'opérateur booléen sont les opérateurs AND et OR. Ces opérateurs booléens sont également binaires, mais servent à combiner deux valeurs booléennes entre elles. La valeur renvoyée est calculée de la même manière que pour les opérateurs < <= et =, en sautant à des parties du code dédiées au renvoie de valeur une fois qu'on a évalué les opérandes. La particularité de ces opérateurs est qu'une optimisation peut être faite pour éviter de calculer les deux opérandes, en fonction de la valeur de la première opérande calculée. Par exemple, pour l'opérateur AND, une première opérande valant 0 signifie que l'expression booléen vaudra 0 dans tous les cas. On peut se servir de cette optimisation afin de passer la section qui calcule la valeur de la deuxième opérande.

Un exemple serait par exemple le code
```
(a < b) AND (c < d)
```
Qui est compilé en
```
{Code première opérande}
pop eax                 ; La valeur de la première opérande a été empilée auparavant
jz and_false_2          ; Si elle vaut faux, on renvoie directement faux
{Code deuxième opérande}
pop ebx
jz and_false_2
push 1
jmp and_end_2
and_false_2:
push 0
and_end_2:
```

Le même principe est appliqué pour l'opérateur OR, lorsque la première opérande vaut vrai, pour éviter de calculer la deuxième opérande.

### Instructions de contrôle

Les deux dernières instructions que le compilateur doit gérer sont l'instruction IF et l'instruction WHILE.

L'instruction IF va simplement exécuter deux morceaux de codes différents en fonction de la valeur d'une expression booléenne donnée. Cela peut facilement être fait en récupérant la valeur de l'expression (dépilement de la pile d'exécution) et en effectuant un saut conditionnel vers la partie du code correspondante.

Un exemple serait le code
```
IF (3 < 4) THEN (output 1) ELSE (output 2).
```
qui est compilé en :
```
mov eax,3 ; Calcul de la condition
push eax
mov eax,4
push eax
pop ebx
pop eax
sub eax, ebx
jge lt_false_2
push 1      
jmp lt_end_2
lt_false_2:
push 0
lt_end_2:
pop eax         ; Récupération de la valeur de la condition
jz if_false_1   
mov eax,1       ; Code si la condition est vraie
push eax
pop eax
out eax
jmp if_end_1
if_false_1:     ; Code si la condition est fausse
mov eax,2
push eax
pop eax
out eax
if_end_1:
```

Enfin, l'instruction while doit répéter un morceau de code tant qu'une condition est vraie. Pour cela, on va donc calculer la condition ; si elle est fausse, alors on saute à la fin de l'instruction. Sinon, on va exécuter le code de la boucle puis sauter au début de l'instruction while, avant le calcule de la condition. On va ainsi répéter l'exécution d'un morceau de code tant que la condition est vraie.

Par exemple, un exemple de boucle simple
```
while (a < 5) do (let a = a + 1) .
```
est compilé en
```
while_start_1:
mov eax,a       ; calcul de la condition
push eax
mov eax,5
push eax
pop ebx
pop eax
sub eax, ebx
jge lt_false_2 
push 1
jmp lt_end_2
lt_false_2:
push 0
lt_end_2:
pop eax
jz while_end_1  ; test de la condition
mov eax,a       ; Code répété
mov ebx,1
add eax, ebx
push eax
pop eax
mov a,eax
jmp while_start_1
while_end_1:
```

### Exemple complet

Un exemple complet de programme utilisant les expressions booléennes est le calcul de pgcd (comme fourni dans le sujet). Avec ce compilateur, le programme
```
let a = input;
let b = input;
while (0 < b)
do (let aux=(a mod b); let a=b; let b=aux );
output a
.
```
est compilé en
```
DATA SEGMENT
a DD
b DD
aux DD
DATA ENDS
CODE SEGMENT
in eax
push eax
pop eax
mov a,eax
in eax
push eax
pop eax
mov b,eax
while_start_1:
mov eax,0
push eax
mov eax,b
push eax
pop ebx
pop eax
sub eax, ebx
jge lt_false_2
push 1
jmp lt_end_2
lt_false_2:
push 0
lt_end_2:
pop eax
jz while_end_1
mov eax,a
mov ebx,b
mov ecx, eax
div eax, ebx
mul eax, ebx
sub ecx, eax
push ecx
pop eax
mov aux,eax
mov eax,b
push eax
pop eax
mov a,eax
mov eax,aux
push eax
pop eax
mov b,eax
jmp while_start_1
while_end_1:
mov eax,a
push eax
pop eax
out eax
CODE ENDS
```

### Optimisations possibles

Comme pour les expressions arithmétiques, il serait possible de définir des cas particuliers pour compiler < <= et = lorsque les opérandes à charger sont des entiers et des identifiants, afin d'éviter un empilement inutile. De plus, il serait possible d'optimiser en avance le code au cas où des conditions sont en réalité constantes.