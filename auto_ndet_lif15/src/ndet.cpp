#include <iostream>
#include <fstream>
#include <sstream>
#include <cstdlib>
#include <string>
#include <algorithm>
#include <vector>
#include <set>
#include <map>
#include <queue>
#include <list>
#include <cassert>
#include <utility>

// pour la seconde partie du projet
#include "expression_rationnelle.hpp"
#include "parser.hpp"

using namespace std;


////////////////////////////////////////////////////////////////////////////////

const unsigned int ASCII_A = 97;
const unsigned int ASCII_Z = ASCII_A + 26;
const bool DEBUG = false;

typedef size_t etat_t;
typedef unsigned char symb_t;
typedef set< etat_t > etatset_t;
typedef vector< vector< etatset_t > > trans_t;
typedef vector< etatset_t > epsilon_t;
typedef map< etatset_t, etat_t > map_t;


////////////////////////////////////////////////////////////////////////////////

struct sAutoNDE {
    // caract�ristiques
    size_t nb_etats;
    size_t nb_symbs;
    size_t nb_finaux;

    etat_t initial;
    // �tat initial

    etatset_t finaux;
    // �tats finaux : finaux_t peut �tre un int*, un tableau dynamique comme vector<int>
    // ou une autre structure de donn�e de votre choix.

    trans_t trans;
    // matrice de transition : trans_t peut �tre un int***, une structure dynamique 3D comme vector< vector< set<int> > >
    // ou une autre structure de donn�e de votre choix.

    epsilon_t epsilon;
    // transitions spontan�es : epsilon_t peut �tre un int**, une structure dynamique 2D comme vector< set<int> >
    // ou une autre structure de donn�e de votre choix.
};

////////////////////////////////////////////////////////////////////////////////

bool FromFile(sAutoNDE& at, string path) {
    //cout << "entr�e dans FromFile" << endl;
    ifstream myfile(path.c_str(), ios::in);
    //un flux d'entree obtenu � partir du nom du fichier
    string line;
    // un ligne lue dans le fichier avec getline(myfile,line);
    istringstream iss;
    // flux associ� � la chaine, pour lire morceau par morceau avec >> (comme cin)
    etat_t s(0), t(0);
    // deux �tats temporaires
    symb_t a(0);
    // un symbole temporaire

    if (myfile.is_open()) {
        // la première ligne donne 'nb_etats nb_symbs nb_finaux'
        do {
            getline(myfile, line);
        } while (line.empty() || line[0] == '#');
        // on autorise les lignes de commentaires : celles qui commencent par '#'
        iss.str(line);
        if ((iss >> at.nb_etats).fail() || (iss >> at.nb_symbs).fail() || (iss >> at.nb_finaux).fail())
            return false;
        // la deuxième ligne donne l'état initial
        do {
            getline(myfile, line);
        } while (line.empty() || line[0] == '#');
        iss.clear();
        iss.str(line);
        if ((iss >> at.initial).fail())
            return -1;

        // les autres lignes donnent les états finaux
        for (size_t i = 0; i < at.nb_finaux; i++) {
            do {
                getline(myfile, line);
            } while (line.empty() || line[0] == '#');
            iss.clear();
            iss.str(line);
            if ((iss >> s).fail())
                continue;
            //        cerr << "s= " << s << endl;
            at.finaux.insert(s);
        }

        // on alloue les vectors � la taille connue � l'avance pour �viter les resize dynamiques
        at.epsilon.resize(at.nb_etats);
        at.trans.resize(at.nb_etats);
        for (size_t i = 0; i < at.nb_etats; ++i)
            at.trans[i].resize(at.nb_symbs);

        // lecture de la relation de transition
        while (myfile.good()) {
            line.clear();
            getline(myfile, line);
            if (line.empty() && line[0] == '#')
                continue;
            iss.clear();
            iss.str(line);

            // si une des trois lectures echoue, on passe � la suite
            if ((iss >> s).fail() || (iss >> a).fail() || (iss >> t).fail() || (a < ASCII_A) || (a > ASCII_Z))
                continue;

            //test espilon ou non
            if ((a - ASCII_A) >= at.nb_symbs) {
                cerr << "s=" << s << ", (e), t=" << t << endl;
                at.epsilon[s].insert(t);
            } else {
                cerr << "s=" << s << ", a=" << a - ASCII_A << ", t=" << t << endl;
                //cout <<"at.trans["<<s<<"]["<<a-ASCII_A<<"].insert("<<t<<")" << endl;
                at.trans[s][a - ASCII_A].insert(t);
            }
        }
        myfile.close();
        return true;
    }
    return false;
    // on ne peut pas ouvrir le fichier
}


// -----------------------------------------------------------------------------
// Fonctions à compléter pour la première partie du projet
// -----------------------------------------------------------------------------


////////////////////////////////////////////////////////////////////////////////

bool ContientFinal(const sAutoNDE& at, const etatset_t& e) {
    // utiliser key_comp()

    etatset_t::key_compare comp = e.key_comp();
    for (etatset_t::const_iterator e_it = e.begin(); e_it != e.end(); e_it++) // on parcourt le tableau d'etats
    {
        for (etatset_t::const_iterator f_it = at.finaux.begin(); f_it != at.finaux.end(); f_it++) // on parcourt le tableau d'etats finaux dans l'automate
        {
            if (!(comp(*e_it, *f_it) || comp(*f_it, *e_it)))
                return true;
        }
    }

    return false;
}


////////////////////////////////////////////////////////////////////////////////

bool EstDeterministe(const sAutoNDE& at) {
    /* on teste si il y a des transitions spontanées, si oui l'automate n'est pas déterministe */
    if (at.epsilon.size() != 0) {
        return false;
    }

    // si le tableau de transitions spontanées est vide, on vérifie les transitions non spontanées.
    for (unsigned int e_it = 0; e_it < at.nb_etats; e_it++) {
        for (unsigned int s_it = 0; s_it < at.nb_symbs; s_it++) {
            if (at.trans[e_it][s_it].size() != 1)
                return false;
        }
    }
    return true;
}

////////////////////////////////////////////////////////////////////////////////

void Fermeture(const sAutoNDE& at, etatset_t& e) {
    // Cette fonction clot l'ensemble d'états E={e_0, e_1, ... ,e_n} passé en
    // paramètre avec les epsilon transitions

    /*Parcours des �tats l'ensemble d'etats */
    for (etatset_t::const_iterator et_it = e.begin(); et_it != e.end(); et_it++) {
        /* parours de l'ensemble des �tats spontan�s de l'�tat actuel*/
        for (etatset_t::const_iterator ep_it = at.epsilon[*et_it].begin(); ep_it != at.epsilon[*et_it].end(); ep_it++)
            e.insert(*ep_it); /* insertion des �tats spobntan�s */
    }
}

////////////////////////////////////////////////////////////////////////////////

etatset_t Delta(const sAutoNDE& at, const etatset_t& e, symb_t c) {
    // définir cette fonction en utilisant Fermeture
    etatset_t etat_retour;
    etatset_t e_ferme; // e étant en const, on forme un état temporaire sur lequel on fera la fermeture
    // On ferme e
    for (etatset_t::const_iterator it = e.begin(); it != e.end(); it++) {
        e_ferme.insert(*it);
    }
    Fermeture(at, e_ferme);

    // on parcours les etats de e_ferme qui est une copie de l'état e fermé
    for (etatset_t::const_iterator e_it = e_ferme.begin(); e_it != e_ferme.end(); e_it++) {
        // on parcours les etats de la transition à partir des états de e et du symbole passé en parametre
        for (etatset_t::const_iterator at_it = at.trans[*e_it][c - ASCII_A].begin(); at_it != at.trans[*e_it][c - ASCII_A].end(); at_it++)
            etat_retour.insert(*at_it); // on insere les etats trouv�s dans l'etatset a renvoyer
    }
    return etat_retour;
}

////////////////////////////////////////////////////////////////////////////////

/* fonction récursive pour accept */
bool AcceptRec(const sAutoNDE& at, string str, etat_t current_state) {
    /* parcours des transitions spontan�es pour l'�tat courant*/
    /* note : il peut y avoir des transitions spontan�es m�me lorsque c'est la derni�re lettre de la chaine, c'est pour cel� que l'on commence par �a */
    for (etatset_t::iterator e_it = at.epsilon[current_state].begin(); e_it != at.epsilon[current_state].end(); e_it++) {
        // appel r�cursif de la fonction sur tout les �tats trouv�s
        if (AcceptRec(at, str, *e_it))
            return true;
    }


    if ((*str.begin() - ASCII_A) >= at.nb_symbs) {
        cout << "la lettre '" << *str.begin() << " n'est pas acceptée par l'automate" << endl;
        return false;
    }
    // condition d'arret
    etatset_t etats_suivants = at.trans[current_state][*(str.begin()) - ASCII_A];
    if (str.size() == 1) {
        if (ContientFinal(at, etats_suivants))
            return true;
        else
            return false;
    } else {
        if (!etats_suivants.empty()) {
            str = str.substr(1, str.size() - 1); // on cr�e une chaine qui est �gale a la chaine sans sa premi�re lettre
            // on parcourt les transitions pour l'etat actuel et la premi�re lettre du mot
            for (etatset_t::iterator f_it = etats_suivants.begin(); f_it != etats_suivants.end(); f_it++) // on parcourt le tableau de transitions dans l'automate
            {
                if (AcceptRec(at, str, *f_it))
                    return true;
            }
        }
    }
    return false;
}

bool Accept(const sAutoNDE& at, string str) {
    // appel sur la fonction r�cursive
    if (AcceptRec(at, str, at.initial))
        return true;
    return false;
}

////////////////////////////////////////////////////////////////////////////////

ostream& operator<<(ostream& out, const sAutoNDE& at) {

    size_t i, j;
    unsigned char charac;
    bool epsilon = false; /* pour savoir si il a des transitions spontan�es*/

    out << "Nombre d'�tats : " << at.nb_etats << endl;
    out << "Nombre de symboles : " << at.nb_symbs << endl;

    out << "Transitions spontan�es : ";

    for (i = 0; i != at.nb_etats; i++) {
        if (at.epsilon[i].size() != 0) {
            epsilon = true;
            break;
        }
    }
    if (epsilon)
        out << "oui";
    else
        out << "non";

    out << endl << "Etat initial : " << at.initial << endl;

    out << "Etats finaux : {";
    int nb_etats_finaux = at.finaux.size();
    for (etatset_t::const_iterator it = at.finaux.begin(); it != at.finaux.end(); it++) {
        nb_etats_finaux--;
        if (nb_etats_finaux == 0)
            out << *it;
        else
            out << *it << ",";
    }
    out << "}" << endl;

    etatset_t transition; /* pour afficher les transitions normales et les transitions spontan�es */

    out << "Transitions :" << endl;
    for (i = 0; i != at.nb_etats; i++) {
        /* parcours des transions normales */
        for (j = 0; j != at.nb_symbs; j++) {
            transition.clear();
            transition.insert(i);
            charac = j + ASCII_A;
            out << " Delta(" << i << "," << charac << ") = {";
            transition = Delta(at, transition, charac);
            int nb_etats = transition.size();
            for (etatset_t::const_iterator t_it = transition.begin(); t_it != transition.end(); t_it++) {
                nb_etats--;
                if (nb_etats == 0)
                    out << *t_it;
                else
                    out << *t_it << ",";
            }

            out << "}" << endl;
        }

        transition.clear();
        transition.insert(i);
        /* parcours des transitions spontan�es */
        if (epsilon) {
            out << " Delta(" << i << ",e) = {";
            Fermeture(at, transition);
            transition.erase(i);
            int nb_etats_epsilon = transition.size();
            for (etatset_t::const_iterator e_it = transition.begin(); e_it != transition.end(); e_it++) {
                nb_etats_epsilon--;
                if (nb_etats_epsilon == 0)
                    out << *e_it;
                else
                    out << *e_it << ",";
            }
            out << "}" << endl;
        }
    }
    return out;
}


////////////////////////////////////////////////////////////////////////////////
/*
sAutoNDE Determinize(const sAutoNDE& at){
        sAutoNDE r;
	

        return r;

} */

typedef map<etat_t, etatset_t > map_etats; /* pour tous �tats, on a un set de transtions*/

sAutoNDE Determinize(const sAutoNDE& at) {
    sAutoNDE r;
    map_t map;
    map_etats map_Epsi, map_Inv;
    etatset_t transition, ENS;
    unsigned int j, k;
    symb_t caractere;
    int indice_Entree, indice, nb_Etats;
    unsigned int MAX = 50;

    r.nb_symbs = at.nb_symbs;
    r.nb_finaux = 0;
    r.trans.resize(MAX);
    for (size_t i = 0; i < MAX; ++i) {
        r.trans[i].resize(r.nb_symbs);
    }

    cout << "Cloture des Epsilons : " << endl;
    for (j = 0; j != at.nb_etats; j++) {
        /*Affichage des epsilon transitions*/
        transition.clear();
        transition.insert(j);
        cout << " E(" << j << ") = {";
        Fermeture(at, transition);
        map_Epsi[j] = transition;
        int nb_etats_spontanes = transition.size();
        for (etatset_t::const_iterator it = transition.begin(); it != transition.end(); it++) {
            nb_etats_spontanes--;
            if (nb_etats_spontanes == 0)
                cout << *it;
            else
                cout << *it << ";";
        }
        cout << "}" << endl;
    }

    ENS = map_Epsi[at.initial];
    map_Inv[0] = ENS;
    map[ENS] = 0;
    r.initial = map[ENS];

    indice_Entree = 0;
    nb_Etats = 1;
    indice = 1;

    // affichage des nouveaux etats

    cout << endl << "Nouveaux Etats : " << endl;
    cout << " {";
    int nb_nv_etats = ENS.size();
    for (etatset_t::const_iterator it2 = ENS.begin(); it2 != ENS.end(); it2++) {
        nb_nv_etats--;
        if (nb_nv_etats == 0)
            cout << *it2;
        else
            cout << *it2 << ",";
    }
    cout << "}      (initial)";
    if (ContientFinal(at, ENS)) {
        r.nb_finaux++;
        r.finaux.insert(map[ENS]);
        cout << "     (final)";
    }
    cout << endl;


    while (indice_Entree < nb_Etats) {
        ENS = map_Inv[indice_Entree];
        for (caractere = ASCII_A; caractere < ASCII_A + at.nb_symbs; caractere++) {
            etatset_t new_ENS;
            ENS = Delta(at, ENS, caractere);
            for (etatset_t::const_iterator it = ENS.begin(); it != ENS.end(); it++) {
                /*etats qu'on peut atteindre avec e */
                for (etatset_t::const_iterator it2 = map_Epsi[*it].begin(); it2 != map_Epsi[*it].end(); it2++) {
                    new_ENS.insert(*it2);
                }
            }

            if (map[new_ENS] == 0) {
                map_Inv[indice] = new_ENS;
                map[new_ENS] = indice;
                nb_Etats++;
                indice++;
                cout << " {";
                int nb = new_ENS.size();
                for (etatset_t::const_iterator it2 = new_ENS.begin(); it2 != new_ENS.end(); it2++) {
                    nb--;
                    if (nb == 0)
                        cout << *it2;
                    else
                        cout << *it2 << ",";
                }
                cout << "}" << endl;

                if (ContientFinal(at, new_ENS)) {
                    r.nb_finaux++;
                    r.finaux.insert(indice);
                }
            }
            r.trans[indice_Entree][caractere - ASCII_A].insert(map[new_ENS]);
            ENS = map_Inv[indice_Entree];
        }

        indice_Entree++;
    }

    // affichage des nouvelles transitions

    r.nb_etats = map.size();
    r.trans.resize(r.nb_etats);

    r.epsilon.resize(r.nb_etats);

    cout << endl << "Nouvelles transitions : " << endl;
    for (j = 0; j != r.nb_etats; j++) {
        /*Affiche des transitions normale*/
        for (k = 0; k != r.nb_symbs; k++) {
            transition.clear();
            transition.insert(j);
            char charac = k + ASCII_A;
            cout << " delta({";
            int nb = map_Inv[j].size();
            for (etatset_t::const_iterator it = map_Inv[j].begin(); it != map_Inv[j].end(); it++) {
                nb--;
                if (nb == 0)
                    cout << *it;
                else
                    cout << *it << ",";
            }
            cout << "}," << charac << ") = {";
            transition = Delta(r, transition, charac);
            for (etatset_t::const_iterator it = transition.begin(); it != transition.end(); it++) {
                int nb = map_Inv[*it].size();
                for (etatset_t::const_iterator it2 = map_Inv[*it].begin(); it2 != map_Inv[*it].end(); it2++) {
                    nb--;
                    if (nb == 0)
                        cout << *it2;
                    else
                        cout << *it2 << ",";
                }
            }
            cout << "}" << endl;
        }
    }
    cout << endl;

    return r;
}


// -----------------------------------------------------------------------------
// Fonctions � compl�ter pour la seconde partie du projet
// -----------------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////////////

bool ToGraph(sAutoNDE& at, string path) {
    //TODO d�finir cette fonction

    return false;
}


////////////////////////////////////////////////////////////////////////////////

// fonction outil : on garde x, et on "ajoute" trans et epsilon de y
// en renommant ses �tats, id est en d�callant les indices des �tats de y
// de x.nb_etats

sAutoNDE Append(const sAutoNDE& x, const sAutoNDE& y) {
    assert(x.nb_symbs == y.nb_symbs);
    sAutoNDE r;

    //TODO d�finir cette fonction

    return r;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Union(const sAutoNDE& x, const sAutoNDE& y) {
    assert(x.nb_symbs == y.nb_symbs);
    sAutoNDE r = Append(x, y);

    //TODO d�finir cette fonction

    return r;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Concat(const sAutoNDE& x, const sAutoNDE& y) {
    assert(x.nb_symbs == y.nb_symbs);
    sAutoNDE r = Append(x, y);

    //TODO d�finir cette fonction

    return r;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Complement(const sAutoNDE& x) {
    //TODO d�finir cette fonction

    return x;
}


////////////////////////////////////////////////////////////////////////////////

sAutoNDE Kleene(const sAutoNDE& x) {
    //TODO d�finir cette fonction

    return x;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Intersection(const sAutoNDE& x, const sAutoNDE& y) {
    //TODO d�finir cette fonction

    return x;
}

////////////////////////////////////////////////////////////////////////////////
/*
sAutoNDE ExpressionRationnelle2Automate(string expr){
  sAutoNDE r;

  sExpressionRationnelle er = lit_expression_rationnelle(expr);

  cout << er << endl;

  //TODO d�finir cette fonction

  return r;
}
 */

////////////////////////////////////////////////////////////////////////////////

void Help(ostream& out, char *s) {
    out << "Utilisation du programme " << s << " :" << endl;
    out << "-acc ou -accept Input Word:\n\t d�termine si le mot Word est accept�" << endl;
    out << "-det ou -determinize Input :\n\t d�terminise Input" << endl;
    out << "-cup ou -union Input1 Input2 :\n\t calcule l'union" << endl;
    out << "-cat ou -concat Input1 Input2 :\n\t calcul la concat�nation" << endl;
    out << "-star ou -kleene Input :\n\t calcul de A*" << endl;
    out << "-bar ou -complement Input :\n\t calcul du compl�ment" << endl;
    out << "-cap ou -intersection Input1 Input2 :\n\t calcul de l'intersection" << endl;
    out << "-expr2aut ou expressionrationnelle2automate ExpressionRationnelle :\n\t calcul de l'automate correspondant � l'expression rationnelle" << endl;
    out << "-nop ou -no_operation Input :\n\t ne rien faire de particulier" << endl;

    out << "-o ou -output Output :\n\t �crire le r�sultat dans le fichier Output, afficher sur STDOUT si non sp�cifi�" << endl;
    out << "-g ou -graphe :\n\t l'output est au format dot/graphiz" << endl << endl;

    out << "Exemple '" << s << " -determinize auto.txt -output determin.txt'" << endl;
}



////////////////////////////////////////////////////////////////////////////////

int main(int argc, char* argv[]) {
    if (argc < 3) {
        Help(cout, argv[0]);
        return EXIT_FAILURE;
    }

    int pos;
    int act = -1; // pos et act pour savoir quelle action effectuer
    int nb_files = 0; // nombre de fichiers en entr�e
    string str, in1, in2, out, acc, expr;
    // chaines pour (resp.) tampon; fichier d'entr�e Input1; fichier d'entr�e Input2;
    // fichier de sortie et chaine dont l'acceptation est � tester
    bool toFile = false, graphMode = false; // sortie STDOUT ou fichier ? Si fichier, format graphviz ?

    // options accept�es
    const size_t NBOPT = 11;
    string aLN[] = {"accept", "determinize", "union", "concat", "kleene", "complement", "intersection", "expressionrationnelle2automate", "no_operation", "output", "graph"};
    string aSN[] = {"acc", "det", "cup", "cat", "star", "bar", "cap", "expr2aut", "nop", "o", "g"};

    // on essaie de "parser" chaque option de la ligne de commande
    for (int i = 1; i < argc; ++i) {
        if (DEBUG) cerr << "argv[" << i << "] = '" << argv[i] << "'" << endl;
        str = argv[i];
        pos = -1;
        string* pL = find(aLN, aLN + NBOPT, str.substr(1));
        string* pS = find(aSN, aSN + NBOPT, str.substr(1));

        if (pL != aLN + NBOPT)
            pos = pL - aLN;
        if (pS != aSN + NBOPT)
            pos = pS - aSN;

        if (pos != -1) {
            // (pos != -1) <=> on a trouv� une option longue ou courte
            if (DEBUG) cerr << "Key found (" << pos << ") : " << str << endl;
            switch (pos) {
                case 0: //acc
                    in1 = argv[++i];
                    acc = argv[++i];
                    nb_files = 1;
                    break;
                case 1: //det
                    in1 = argv[++i];
                    nb_files = 1;
                    break;
                case 2: //cup
                    in1 = argv[++i];
                    in2 = argv[++i];
                    nb_files = 2;
                    break;
                case 3: //cat
                    in1 = argv[++i];
                    in2 = argv[++i];
                    nb_files = 2;
                    break;
                case 4: //star
                    in1 = argv[++i];
                    nb_files = 1;
                    break;
                case 5: //bar
                    in1 = argv[++i];
                    nb_files = 1;
                    break;
                case 6: //cap
                    in1 = argv[++i];
                    in2 = argv[++i];
                    nb_files = 2;
                    break;
                case 7: //expr2aut
                    expr = argv[++i];
                    nb_files = 0;
                    break;
                case 8: //nop
                    in1 = argv[++i];
                    nb_files = 1;
                    break;
                case 9: //o
                    toFile = true;
                    out = argv[++i];
                    break;
                case 10: //g
                    graphMode = true;
                    break;
                default:
                    return EXIT_FAILURE;
            }
        } else {
            cerr << "Option inconnue " << str << endl;
            return EXIT_FAILURE;
        }

        if (pos < 9) {
            if (act > -1) {
                cerr << "Plusieurs actions sp�cifif�es" << endl;
                return EXIT_FAILURE;
            } else
                act = pos;
        }
    }

    if (act == -1) {
        cerr << "Pas d'action sp�cifif�e" << endl;
        return EXIT_FAILURE;
    }

    /* Les options sont OK, on va essayer de lire le(s) automate(s) at1 (et at2)
    et effectuer l'action sp�cifi�e. Atr stockera le r�sultat*/

    sAutoNDE at1, at2, atr;

    if ((nb_files == 1 or nb_files == 2) and !FromFile(at1, in1)) {
        cerr << "Erreur de lecture " << in1 << endl;
        return EXIT_FAILURE;
    }
    if (nb_files == 2 and !FromFile(at2, in2)) {
        cerr << "Erreur de lecture " << in2 << endl;
        return EXIT_FAILURE;
    }

    //cout << "sortie de formFile";

    switch (act) {
        case 0: //acc
            cout << "'" << acc << "' est accept�: " << Accept(at1, acc) << endl;
            atr = at1;
            break;
        case 1: //det
            atr = Determinize(at1);
            break;
        case 2: //cup
            atr = Union(at1, at2);
            break;
        case 3: //cat
            atr = Concat(at1, at2);
            break;
        case 4: //star
            atr = Kleene(at1);
            break;
        case 5: //bar
            atr = Complement(at1);
            break;
        case 6: //cap
            atr = Intersection(at1, at2);
            break;
        case 7: //expr2aut
            //    atr =  ExpressionRationnelle2Automate(expr);
            break;
        case 8: //nop
            atr = at1;
            break;
        default:
            return EXIT_FAILURE;
    }

    // on affiche le r�sultat ou on l'�crit dans un fichier
    if (!toFile)
        cout << atr;
    else {
        if (graphMode) {
            ToGraph(atr, out + ".gv");
            system(("dot -Tpng " + out + ".gv -o " + out + ".png").c_str());
        } else {
            ofstream f((out + ".txt").c_str(), ios::trunc);
            if (f.fail())
                return EXIT_FAILURE;
            f << atr;
        }
    }

    return EXIT_SUCCESS;
}



