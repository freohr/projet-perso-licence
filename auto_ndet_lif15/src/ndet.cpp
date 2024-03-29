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
#include <assert.h>

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
    // caractéristiques
    size_t nb_etats;
    size_t nb_symbs;
    size_t nb_finaux;

    etat_t initial;
    // État initial

    etatset_t finaux;
    // États finaux : finaux_t peut être un int*, un tableau dynamique comme vector<int>
    // ou une autre structure de données de votre choix.

    trans_t trans;
    // matrice de transition : trans_t peut être un int***, une structure dynamique 3D comme vector< vector< set<int> > >
    // ou une autre structure de données de votre choix.

    epsilon_t epsilon;
    // transitions spontan�es : epsilon_t peut être un int**, une structure dynamique 2D comme vector< set<int> >
    // ou une autre structure de données de votre choix.
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
                //cerr << "s=" << s << ", (e), t=" << t << endl;
                at.epsilon[s].insert(t);
            } else {
                //cerr << "s=" << s << ", a=" << a - ASCII_A << ", t=" << t << endl;
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


    /*Parcours des états l'ensemble d'etats */

    for (etatset_t::const_iterator et_it = e.begin(); et_it != e.end(); et_it++) {
        /* parours de l'ensemble des états spontanés de l'état actuel*/
        for (etatset_t::const_iterator ep_it = at.epsilon[*et_it].begin(); ep_it != at.epsilon[*et_it].end(); ep_it++)
            /* insertion des états spontanés */
            e.insert(*ep_it);
    }
}

////////////////////////////////////////////////////////////////////////////////

etatset_t Delta(const sAutoNDE& at, const etatset_t& e, symb_t c) {
    // définir cette fonction en utilisant Fermeture
    etatset_t etat_retour;
    etatset_t tmp_ferme; // e étant en const, on forme un état temporaire sur lequel on fera la fermeture

    // On ferme e
    for (etatset_t::const_iterator it = e.begin(); it != e.end(); it++) {
        tmp_ferme.insert(*it);
    }
    Fermeture(at, tmp_ferme);

    // on parcourt les etats de e_ferme qui est une copie de l'état e fermé
    for (etatset_t::const_iterator tmp_it = tmp_ferme.begin(); tmp_it != tmp_ferme.end(); tmp_it++) {
        // on parcourt les états de la transition à partir des états de e et du symbole passé en paramètre
        for (etatset_t::const_iterator at_it = at.trans[*tmp_it][c - ASCII_A].begin(); at_it != at.trans[*tmp_it][c - ASCII_A].end(); at_it++)
            etat_retour.insert(*at_it); // on insere les etats trouvés dans l'etatset a renvoyer
    }

    return etat_retour;
}

////////////////////////////////////////////////////////////////////////////////

/* fonction récursive pour accept */
bool AcceptRec(const sAutoNDE& at, string str, etat_t current_state) {
    /* parcours des transitions spontanées pour l'état courant*/
    /* note : il peut y avoir des transitions spontanées même lorsque c'est la dernière lettre de la chaine, c'est pour celà que l'on commence par ça */
    for (etatset_t::iterator e_it = at.epsilon[current_state].begin(); e_it != at.epsilon[current_state].end(); e_it++) {
        // appel récursif de la fonction sur tout les états trouvés
        if (AcceptRec(at, str, *e_it))
            return true;
    }


    if ((*str.begin() - ASCII_A) >= at.nb_symbs) {
        cout << "la lettre '" << *str.begin() << " n'est pas acceptée par l'automate" << endl;
        return false;
    }
    // condition d'arrêt
    etatset_t etats_suivants = at.trans[current_state][*(str.begin()) - ASCII_A];
    if (str.size() == 1) {
        if (ContientFinal(at, etats_suivants))
            return true;
        else
            return false;
    } else {
        if (!etats_suivants.empty()) {
            str = str.substr(1, str.size() - 1); // on crée une sous-chaine qui est égale à la chaine sans sa première lettre
            // on parcourt les transitions pour l'état actuel et la première lettre du mot
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
    bool epsilon = false; /* pour savoir s'il y a des transitions spontanées*/

    out << "Nombre d'états : " << at.nb_etats << endl;
    out << "Nombre de symboles : " << at.nb_symbs << endl;

    out << "Transitions spontanées : ";

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

    etatset_t transition; /* pour afficher les transitions normales et les transitions spontanées */

    out << "Transitions :" << endl;
    for (i = 0; i != at.nb_etats; i++) {
        /* parcours des transitions normales */
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
        /* parcours des transitions spontanées */
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

typedef map<etat_t, etatset_t > map_etats; /* pour tout état, on a un set de transtions*/

/* vérifie si le set1 est avant le set2 dans l'ordre lexicographique */
bool isBefore(etatset_t &set1, etatset_t& set2) {
    etatset_t::const_iterator it1 = set1.begin();
    etatset_t::const_iterator it2 = set2.begin();

    if(set1.size() > set2.size())
        return true;

    while (it1 != set1.end() && it2 != set2.end()) {
        if (*it1 < *it2)
            return true;
        else if (*it1 > *it2)
            return false;

        it1++;
        it2++;

    }

    if (it1 == set1.end())
        return false;
    else
        return true;
}

bool equals(etatset_t &set1, etatset_t &set2) {
	etatset_t::const_iterator it1 = set1.begin();
    etatset_t::const_iterator it2 = set2.begin();

    if(set1.size() != set2.size())
        return false;

	while (it1 != set1.end() && it2 != set2.end()) {
        if (*it1 != *it2)
            return false;

        it1++;
        it2++;
	}

    return true;
}

map_etats ordonne_lexico(map_etats &map_inv) {
    map_etats temp_map;
    vector<etatset_t> temp_vector;

    map_etats::iterator inv_it = map_inv.begin();
    vector<etatset_t>::iterator tmp_it = temp_vector.begin();

    for (inv_it = map_inv.begin(); inv_it != map_inv.end(); inv_it++) {

        if (temp_vector.size() == 0) {
            temp_vector.insert(temp_vector.begin(), inv_it->second);
        } else {
            for (tmp_it = temp_vector.begin(); tmp_it != temp_vector.end(); tmp_it++) {

                bool before = isBefore(inv_it->second, *tmp_it);
                if (before) {
                    temp_vector.insert(tmp_it, inv_it->second);
                    break;
                }
            }
            if(tmp_it == temp_vector.end())
                temp_vector.insert(tmp_it, inv_it->second);
        }
    }

    for (etat_t i = 0; i < temp_vector.size(); i++) {
        temp_map.insert(std::pair<etat_t, etatset_t>(i, temp_vector.at(i)));
    }

    return temp_map;
}

sAutoNDE Determinize(const sAutoNDE& at) {
	if (EstDeterministe(at))
		return at;
		
    sAutoNDE r; /*automate de retour, déterminisé */
    map_t map_auto_det;
    map_etats map_Epsi, map_Inv;
    etatset_t transition, ENS;
    symb_t caractere;

    unsigned int j, k;
    int indice_Entree, indice, nb_Etats;
    unsigned int MAX = 50;

    r.nb_symbs = at.nb_symbs;
    r.nb_finaux = 0;
    r.trans.resize(MAX);
    for (size_t i = 0; i < MAX; ++i) {
        r.trans[i].resize(r.nb_symbs);
    }


    // Étape 1 : Epsilon-clôture
    cout << "Epsilon-clotures : " << endl;

    for (j = 0; j != at.nb_etats; j++) {
        /*Affichage des epsilon transitions*/
        transition.clear();
        transition.insert(j);

        cout << " E(" << j << ") = {";

        Fermeture(at, transition);
        map_Epsi[j] = transition; /* Ajout de l'équivalence entre l'état et son epsilon-clôture*/

        int nb_etats_spontanes = transition.size();
        for (etatset_t::const_iterator it = transition.begin(); it != transition.end(); it++) {
            nb_etats_spontanes--;
            if (nb_etats_spontanes == 0)
                cout << *it;
            else
                cout << *it << ",";
        }
        cout << "}" << endl;
    }


    // Étape 2 : calcul des nouveaux états nécéssaires à la déterminisation
    ENS = map_Epsi[at.initial]; /* L'état de départ correspond à l'e-clôture de l'état initial de l'automate non-det*/
    map_Inv[0] = ENS;
    map_auto_det[ENS] = 0; /*l'ensemble d'états correspondant à l'état initial a pour indice 0 parmi les états de l'automate déterminisé */
    r.initial = map_auto_det[ENS];

    indice_Entree = 0;
    nb_Etats = 1;
    indice = 1;

    // Calcul des nouveaux états

    while (indice_Entree < nb_Etats) {
        ENS = map_Inv[indice_Entree];

        /* on cherche les transitions pour chaque symbole du langage */
        for (caractere = ASCII_A; caractere < ASCII_A + at.nb_symbs; caractere++) {

            etatset_t new_ENS;
            ENS = Delta(at, ENS, caractere);

            for (etatset_t::const_iterator it = ENS.begin(); it != ENS.end(); it++) {
                /* ajout des etats atteignable par transition spontanée */
                for (etatset_t::const_iterator it2 = map_Epsi[*it].begin(); it2 != map_Epsi[*it].end(); it2++) {
                    new_ENS.insert(*it2);
                }
            }

            if (map_auto_det[new_ENS] == 0) {
                map_Inv[indice] = new_ENS;
                map_auto_det[new_ENS] = indice;
                nb_Etats++;
                indice++;

                /*cout << " {";
                int nb = new_ENS.size();
                for (etatset_t::const_iterator it2 = new_ENS.begin(); it2 != new_ENS.end(); it2++) {
                    nb--;
                    if (nb == 0)
                        cout << *it2;
                    else
                        cout << *it2 << ",";
                }
                cout << "}"; */
/*
                if (ContientFinal(at, new_ENS)) {
                    r.nb_finaux++;
                    r.finaux.insert(indice);
                    cout << "    (final)";
                }

                cout << endl;*/
            }
            r.trans[indice_Entree][caractere - ASCII_A].insert(map_auto_det[new_ENS]);
            ENS = map_Inv[indice_Entree];
        }

        indice_Entree++;
    }


    // affichage des nouveaux etats

    map_Inv = ordonne_lexico(map_Inv);

    cout << endl << "Nouveaux Etats : " << endl;

    for (unsigned int i = 0; i < map_Inv.size(); i++) {
        cout << " {";
        int nb_nv_etats = map_Inv[i].size();
        for (etatset_t::const_iterator it2 = map_Inv[i].begin(); it2 != map_Inv[i].end(); it2++) {
            nb_nv_etats--;
            if (nb_nv_etats == 0)
                cout << *it2;
            else
                cout << *it2 << ",";
        }
        cout << "}";
        if (equals(map_Epsi[0], map_Inv[i]))
            cout << "      (initial)";
        if (ContientFinal(at, map_Inv[i])) {
            r.nb_finaux++;
            r.finaux.insert(map_auto_det[map_Inv[i]]);
            cout << "     (final)";
        }
        cout << endl;
    }
    // affichage des nouvelles transitions

    r.nb_etats = map_auto_det.size();
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
// Fonctions à compléter pour la seconde partie du projet
// -----------------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////////////

bool ToGraph(sAutoNDE& at, string path) {
    //TODO définir cette fonction
    
    ofstream sortie;
    sortie.open(path.c_str());
	
    if(!sortie.is_open())
    {
		cerr << "Erreur d'ouverture du fichier " << path << " pour la création graphique de l'automate" << endl;
		return false;
	}
	
    sortie << "digraph finite_state_machine {" << endl;
    sortie << "	rankdir=LR;" << endl;
    sortie << "	size=\"10,10\";" << endl;
    sortie << endl;
	
    // dessin des états finaux sous forme de double cercle
    for(etatset_t::const_iterator f_it = at.finaux.begin(); f_it != at.finaux.end(); f_it++)
		sortie << "	node [shape = doublecircle]; "<< *f_it <<" ;"<<endl;

	sortie << "	node [shape = point ]; q;" << endl;
	sortie << "	node [shape = circle];" << endl;

	sortie << "	q -> " << at.initial << ";" << endl;

	//transitions non spontanées
	// 0 -> 2 [label = "b"];
	for(unsigned int j = 0; j < at.trans.size(); j++) {
        for(unsigned int i = 0; i < at.trans.at(j).size(); i++) {			
            for(etatset_t::const_iterator eset_it = at.trans.at(j).at(i).begin(); eset_it != at.trans.at(j).at(i).end(); eset_it++)
                sortie << "	" << j << " -> " << (*eset_it) << " [label = \"" << (char)(ASCII_A + i) << "\"];" << endl;
        }
	}

	//transitions spontanées
	//0 -> 1 [label = "ε"];	
	for(unsigned int i = 0; i < at.epsilon.size(); i++) {		
        for(etatset_t::const_iterator esp_it2 = at.epsilon.at(i).begin(); esp_it2 != at.epsilon.at(i).end(); esp_it2++) {
            sortie << "	" << i << " -> " << (*esp_it2) << " [label = \"ε\"];" << endl;
        }
    }

	sortie << endl << endl << "}";

    return true;
}
////////////////////////////////////////////////////////////////////////////////

// Fonction outil : copie d'un automate
sAutoNDE Copie(const sAutoNDE& x) {

	sAutoNDE tmp;
	
	tmp.nb_etats = x.nb_etats;
	tmp.nb_symbs = x.nb_symbs;
	tmp.nb_finaux = x.nb_finaux;

	tmp.initial = x.initial;
	tmp.finaux = x.finaux;
	tmp.trans = x.trans;
	tmp.epsilon = x.epsilon;

	return tmp;
}

////////////////////////////////////////////////////////////////////////////////

// fonction outil : on garde x, et on "ajoute" trans et epsilon de y
// en renommant ses �tats en d�callant les indices des �tats de y
// de x.nb_etats

// ATTENTION : Append ne redéfinis pas d'état initial
// penser a changer l'état inital dans les fonction utilisant Append
sAutoNDE Append(const sAutoNDE& x, const sAutoNDE& y) {
    assert(x.nb_symbs == y.nb_symbs); // on vérifie que les deux alphabets sont égaux
	cout << "entrée dans append" << endl;
    sAutoNDE r;

	r.nb_etats = x.nb_etats + y.nb_etats;
	r.nb_symbs = x.nb_symbs;
	r.nb_finaux = x.nb_finaux + y.nb_finaux;
	//r.initial = x.initial;

	// on redéfinis la taille des tableaux
	r.epsilon.resize(r.nb_etats);
    r.trans.resize(r.nb_etats);
    for(unsigned int i = 0; i<r.nb_etats; ++i)
		r.trans[i].resize(r.nb_symbs);
		
	cout << "resize " << endl;
	// on décale les transitions de l'automate y

	// on ajoute les transitions de x et y
	for(unsigned int i = 0; i<r.nb_etats; i++)
	{
		cout << "trans " << i << endl;
		if (i<x.nb_etats)
		{
			r.epsilon[i] = x.epsilon[i];
			r.trans[i] = x.trans[i];
		}
		else
		{
			for(etatset_t::const_iterator ye_it = y.epsilon[i-x.nb_etats].begin(); ye_it != y.epsilon[i-x.nb_etats].end(); ye_it ++)
				r.epsilon[i].insert(x.nb_etats + (*ye_it));
			for(unsigned int j = 0; j < y.trans[i-x.nb_etats].size(); j++)
			{
				for(etatset_t::const_iterator yt_it = y.trans[i-x.nb_etats][j].begin(); yt_it != y.trans[i-x.nb_etats][j].end(); yt_it ++)
					r.trans[i][j].insert(x.nb_etats + (*yt_it));
			}
		}
	}

	cout << "transitions" << endl;

	// on ajoute les états finaux
	for(etatset_t::const_iterator x_it = x.finaux.begin(); x_it != x.finaux.end(); x_it ++)
	{
		r.finaux.insert(*x_it);
	}
	for(etatset_t::const_iterator y_it = y.finaux.begin(); y_it != y.finaux.end(); y_it ++)
	{
		r.finaux.insert(x.nb_etats+ (*y_it));
	}

	cout << "finaux" << endl;
	r.epsilon.resize(r.nb_etats);
   	r.trans.resize(r.nb_etats);
	for(unsigned int i = 0; i< r.trans.size(); i++) {
		r.trans[i].resize(r.nb_symbs);
	}

    return r;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Union(const sAutoNDE& x, const sAutoNDE& y) {
    assert(x.nb_symbs == y.nb_symbs);
    sAutoNDE r = Append(x, y);

	// création de l'etat initial
	r.nb_etats++; // on rajoute l'état final à la fin
	r.initial = r.nb_etats-1;
	
	// on crée deux transitions spontanées entre le nouvel état initial
	// et les états initiaux des deux automates
	etatset_t transitions_etat_initial;
	transitions_etat_initial.insert(x.initial);
	transitions_etat_initial.insert(x.nb_etats+y.initial);
	r.epsilon.push_back(transitions_etat_initial);
	r.trans.resize(r.nb_etats);
	for(unsigned int i = 0; i< r.trans.size(); i++) {
		r.trans[i].resize(r.nb_symbs);
	}

    return r;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Concat(const sAutoNDE& x, const sAutoNDE& y) {
   cout << "rentré dans concat" << endl; 
    assert(x.nb_symbs == y.nb_symbs);
    sAutoNDE r = Append(x, y);
	
   cout << "append ok" << endl; 
    r.initial = x.initial;
	// on remplace les états finaux de x par des transitions spontanées
	// sur l'etat inital de y
	for (etatset_t::const_iterator xf_it = x.finaux.begin(); xf_it != x.finaux.end(); xf_it ++)
	{
		//cout << "xf_it = " << (*xf_it) << endl;
		r.epsilon[(*xf_it)].insert(y.initial + x.nb_etats);
		r.finaux.erase(*xf_it);
	}

	cout << "sortie de concat" << endl;
	
	r.epsilon.resize(r.nb_etats);
   	r.trans.resize(r.nb_etats);
	for(unsigned int i = 0; i< r.trans.size(); i++) {
		r.trans[i].resize(r.nb_symbs);
	}
	
    return r;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Complement(const sAutoNDE& x) {
	sAutoNDE r = x;
	etatset_t finaux_tmp;
	bool est_final = false;
	
	for (unsigned int i = 0; i<r.nb_etats; i++)
	{
		for(etatset_t::iterator rf_it = r.finaux.begin(); rf_it != r.finaux.end(); rf_it ++)
		{
			if ((*rf_it) == i) // si l'etat actuel est un état final
				est_final = true;
		}
		if(!est_final)
			finaux_tmp.insert(i);
		est_final = false;
	}
	r.finaux = finaux_tmp;
	r.nb_finaux = r.finaux.size();
    return r;
}


////////////////////////////////////////////////////////////////////////////////

sAutoNDE Kleene(const sAutoNDE& x) {
    //TODO d�finir cette fonction

	//copie de l'automate
	sAutoNDE tmp = Copie(x);
	//fprintf(stderr, "automate copié\n");

	//transition spontanée des états finaux sur l'état initial
	for(etatset_t::const_iterator fin_t = tmp.finaux.begin(); fin_t != tmp.finaux.end(); fin_t++) {
		tmp.epsilon.at(*fin_t).insert(tmp.initial);
		//fprintf(stderr, "transition de %d ok\n", *fin_t);
	}


	//ajout d'un nouvel état initial + transition spontanée sur l'ancien état initial

	//fprintf(stderr, "nb états %d\n", tmp.nb_etats);
	tmp.nb_etats++;
	//fprintf(stderr, "nb états apres ++ %d\n", tmp.nb_etats);
	tmp.epsilon.resize(tmp.nb_etats);
	//fprintf(stderr, "resize ok\n");

	tmp.epsilon[x.nb_etats].insert(tmp.initial);
	//fprintf(stderr, " epsilon nouveau ->ancien ok\n");

	tmp.initial = tmp.nb_etats-1;
	//fprintf(stderr, "changement init ok\n");
	tmp.finaux.insert(tmp.initial);
	//fprintf(stderr, "ajout nouveau init dans finaux ok\n");
	
	tmp.epsilon.resize(tmp.nb_etats);
   	tmp.trans.resize(tmp.nb_etats);
	for(unsigned int i = 0; i< tmp.trans.size(); i++) {
		tmp.trans[i].resize(tmp.nb_symbs);
	}
	
    return tmp;
}

////////////////////////////////////////////////////////////////////////////////

sAutoNDE Intersection(const sAutoNDE& x, const sAutoNDE& y) {
    sAutoNDE a = Complement(x);
    sAutoNDE b = Complement(y);
    sAutoNDE r = Union(a, b);
    return Complement(r);
}

////////////////////////////////////////////////////////////////////////////////
sAutoNDE Expr2AutRecur(sExpressionRationnelle& er) {
	switch(er->op) 
	{
		case o_variable:{
			cout << "cas variable" << endl;
			int var = ((er->nom)->at(0)) - ASCII_A; 
			cout << "var " << ((er->nom)->at(0)) << ": " << var << endl; 	
			sAutoNDE tmp;

			tmp.nb_etats = 2;
			tmp.nb_symbs = var+1; 
			tmp.nb_finaux = 1;
			tmp.initial = 0;
			tmp.finaux.insert(1);
			cout << "insert finaux ok " << endl;
			
       		tmp.trans.resize(tmp.nb_etats);
       		for (size_t i = 0; i < tmp.nb_etats; ++i){
				tmp.trans[i].resize(tmp.nb_symbs);
				cout << "resize " << i << " ok" << endl;
			}
			tmp.epsilon.resize(tmp.nb_etats);

			cout << "tmp init : " << tmp.initial << endl;
			cout << "variable : "<< ((er->nom)->at(0)) - ASCII_A << endl;
			tmp.trans[tmp.initial][var].insert(1);
			cout << "insert trans ok" << endl;
			cout << "var ok" << endl<<endl;
			
			cout << "affichage graph" << endl;
			cout << tmp;
			cout << endl;	
			return tmp;

			break;}

		case o_ou:{
			cout << "cas ou" << endl;
			sAutoNDE tmp1 = Expr2AutRecur(er->arg1);
			sAutoNDE tmp2 = Expr2AutRecur(er->arg2);
			sAutoNDE tmp;
			if(tmp1.nb_symbs != tmp2.nb_symbs) {
				tmp1.nb_symbs = max(tmp1.nb_symbs, tmp2.nb_symbs);
				tmp2.nb_symbs = max(tmp1.nb_symbs, tmp2.nb_symbs);
			}
			tmp = Union(tmp1, tmp2);
			cout << "union ok" << endl << endl;
			cout << "aut union" << endl;
			cout << "taille du tableau de transitions : " <<tmp.trans.size() << endl;
			cout << tmp;
			cout << endl;

			return tmp;
			break;}

		case o_concat:{
			cout << "cas concat" << endl;
			sAutoNDE tmp1 = Expr2AutRecur(er->arg1);
			sAutoNDE tmp2 = Expr2AutRecur(er->arg2);
			if(tmp1.nb_symbs != tmp2.nb_symbs) {
				tmp1.nb_symbs = max(tmp1.nb_symbs, tmp2.nb_symbs);
				tmp2.nb_symbs = max(tmp1.nb_symbs, tmp2.nb_symbs);
			}

			cout << "concat ok" << endl << endl;
			sAutoNDE tmp = Concat(tmp1, tmp2);
			cout << "aut concat" << endl;
			cout << tmp;
			cout << endl;
			
			cout << "nombre d'etats : " << tmp.nb_etats << endl;
			cout << "taille du tableau de transitions : " <<tmp.trans.size() << endl;
		
			return tmp;
			break;}

		case o_etoile:{
			cout << "cas etoile" << endl;
			sAutoNDE tmp = Expr2AutRecur(er->arg);
			cout << "étoile ok" << endl << endl;
			return Kleene(tmp);
			break;}
	}
	
	sAutoNDE test;
	return test;
}


sAutoNDE ExpressionRationnelle2Automate(string expr){

		sExpressionRationnelle er = lit_expression_rationnelle(expr);

		cout << er << endl;
		
		sAutoNDE r = Expr2AutRecur(er);

		cout << "apres transfo exp" << endl;
		cout << "nombre d'etats : " << r.nb_etats << endl;
		cout << "taille du tableau de transitions : " <<r.trans.size() << endl;
		cout << r << endl;
		//TODO d�finir cette fonction
		return r;
}

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
    et effectuer l'action spécifiée. Atr stockera le r�sultat*/

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
            cout << "'" << acc << "' est accepté: " << Accept(at1, acc) << endl;
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
            atr =  ExpressionRationnelle2Automate(expr);
            break;
        case 8: //nop
            atr = at1;
            break;
        default:
            return EXIT_FAILURE;
    }
    // on affiche le r�sultat ou on l'�crit dans un fichier
    if (!toFile) {
        cout << endl << atr;
    } else {
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



