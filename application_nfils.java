import java.awt.*;
import java.util.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.text.*;
public class application_nfils{
    static appli_nfils applic_nfils;
    public static void main(String[] args) {
	applic_nfils = new appli_nfils("Fenetre de choix de programmes");
	applic_nfils.run();
    }
}
class appli_nfils extends Frame{
    static final float pi=(float)3.141592652;
    static final float eps0=1/(36*pi*10*(float)Math.pow((float)10000.0,2));
    static final int dim=63;
    static int xpos=5,xneg=15,dxposneg=3,dyposneg=1,dipole=1, quadripole=2, dernier=3, choix_sur_disque=4, creer_ensemble=5, ecrire_sur_disque=6, creer=2, eliminer=3, montrer_champs=4;
    Image image,image2;boolean ensemble_identiques=false;
     static final int top_demarre = 360,left_demarre = 50,bottom_demarre = 480,right_demarre = 650;
    point zer;
    public ensemble_de_cylindres ens_de_cyl[]=new ensemble_de_cylindres[2];
    int ppmouseh;int ppmousev;boolean relachee,pressee,cliquee,draguee;
    boolean vas_y=false,demo_a_faire=true;long toto_int=0;
    long temps_en_sec=0;int i_run;boolean creation_cylindres;
    long temps_initial_en_secondes,temps_initial_en_secondes_prec=0,temps_maximum=3600;
    Graphics gr;
    boolean comm_on=false;
    float cosinus[]=new float[64];float sinus[]=new float[64];
    float cocos[][]=new float [64][8];float sisin[][]=new float [64][8];
    private SimpleDateFormat formatter;String d_ou_je_reviens;
    private MouseMotion motion;Color cuivre;
    boolean terminer_demo=false;
    int n_ensembles;matrice mat_e_tang;
    private MouseStatic mm;
    boolean toutdebut,run_applet,dessiner_menu_principal_ou_fin;
    Font times14=new Font("Times",Font.PLAIN,14);
    Font times_gras_14=new Font("Times",Font.BOLD,14);
    Font times_gras_24=new Font("Times",Font.BOLD,24);
    appli_nfils (String s){
	super(s);
        addWindowListener(new java.awt.event.WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent e) {
		    on_s_en_va();
		    dispose();
		    System.exit(0);
		};
	    });
	toutdebut=true;
	run_applet=true;dessiner_menu_principal_ou_fin=false;
	System.out.println("init applet");
	mm=new MouseStatic(this);
	this.addMouseListener(mm);
	motion=new MouseMotion(this); setBackground(Color.white);
	this.addMouseMotionListener(motion);
	formatter = new SimpleDateFormat ("EEE MMM dd hh:mm:ss yyyy", Locale.getDefault());
	Date maintenant=new Date();cuivre=new Color(180,90,0);
	temps_initial_en_secondes=temps_en_secondes(maintenant);
	temps_initial_en_secondes_prec=temps_initial_en_secondes;
	System.out.println("maintenant "+maintenant+" s "+temps_initial_en_secondes);
	creation_cylindres=true;d_ou_je_reviens="";
	zer=new point((float)0.,(float)0.);
	for (int k=0;k<dim;k++){
	    cosinus[k]=(float)Math.cos(k*(float)2.0*pi/dim);
	    sinus[k]=(float)Math.sin(k*(float)2.0*pi/dim);
	}
	for (int i=0;i<8;i++)
	    for (int k=0;k<dim;k++){
		cocos[k][i]=(float)Math.cos((i+1)*(k*(float)2.0*pi/dim));
		sisin[k][i]=(float)Math.sin((i+1)*(k*(float)2.0*pi/dim));
	    }

	mat_e_tang= new matrice(dim);
	for (int j=0;j<dim;j++){
	    for (int k=0;k<dim;k++){
		if(j!=k){
		    float distx=(cosinus[j]-cosinus[k]);
		    float disty=(sinus[j]-sinus[k]);
		    float dist=(float)Math.pow(disty,2)+(float)Math.pow(distx,2);
		    //mat_e_tang.directe[k][j]=(rayon*2.0*pi/dim)/(2*pi*dist)*(-distx*ss+disty*cc);
		    if(j!=0)
			mat_e_tang.directe[j][k]=(-distx*sinus[j]+disty*cosinus[j])/(dim*dist)/eps0;//*rayon
		    else
			mat_e_tang.directe[j][j]=0;			//for ii=1 to dim do
		}
	    }
	    for (int k=0;k<dim;k++)
		mat_e_tang.directe[0][k]=(float)1.0;
	    
	    if(j==1)System.out.println("j "+j+" "+mat_e_tang.directe[j][0]+" "+mat_e_tang.directe[j][1]+" "+mat_e_tang.directe[j][2]);
	    if(j>23)System.out.println("j "+j+" "+mat_e_tang.directe[j][24]+" "+mat_e_tang.directe[j][25]+" "+mat_e_tang.directe[j][26]+" "+mat_e_tang.directe[j][27]+" "+mat_e_tang.directe[j][28]+" "+mat_e_tang.directe[j][29]+" "+mat_e_tang.directe[j][30]);
	}
	//pour eviter les pivots nuls.
	System.out.println("j "+1+" "+mat_e_tang.directe[1][0]+" "+mat_e_tang.directe[1][1]+" "+mat_e_tang.directe[1][2]);
	//for (int j=24;j<dim;j++)
	//  if(j>23)System.out.println("j "+j+" "+mat_e_tang.directe[j][24]+" "+mat_e_tang.directe[j][25]+" "+mat_e_tang.directe[j][26]+" "+mat_e_tang.directe[j][27]+" "+mat_e_tang.directe[j][28]+" "+mat_e_tang.directe[j][29]+" "+mat_e_tang.directe[j][30]);
	
	mat_e_tang.invers();
	mat_e_tang.somme_lignes();			    

	pack();setVisible(true);	
	setSize(1100,700);
	setLocation(0,0);
	gr= getGraphics();
	String name="C:/Users/benoit Delcourt/Desktop/j2sdk1.4.2_04/dev/nfils_premiere_page.jpg";
	image=createImage(400,400);
	Graphics gTTampon=image.getGraphics();
	image=Toolkit.getDefaultToolkit().getImage(name);
	MediaTracker tracker=new MediaTracker(this);
	tracker.addImage(image,1); 
	String name2="C:/Users/benoit Delcourt/Desktop/j2sdk1.4.2_04/dev/nfils_methode.jpg";
	image2=createImage(300,400);
	Graphics gTTampon2=image2.getGraphics();
	image2=Toolkit.getDefaultToolkit().getImage(name2);
	tracker.addImage(image2,0); 
	try {tracker.waitForAll(); }
	catch (InterruptedException e) {
	    System.out.println(" image2 pas arrivee?");
	}
	//gr.drawImage(image2,0,420,null);
	//gr.drawImage(image,630,60,null);
    }
   public long temps_en_secondes(Date nun){
	formatter.applyPattern("s");
	int s = Integer.parseInt(formatter.format(nun));
	formatter.applyPattern("m");
	int m = Integer.parseInt(formatter.format(nun));
	formatter.applyPattern("h");
	int h = Integer.parseInt(formatter.format(nun));
	//System.out.println(" h "+h+" m "+m+" s "+s);
	return (h*3600+m*60+s);
    }    
    public void run(){

	int isleep=2;
	//menu_principal_et_fin();
	fin_de_programme:
	while (run_applet){
	    Date now=new Date();
	    temps_en_sec=temps_en_secondes(now);
	    //System.out.println("temps_en_sec "+temps_en_sec);
	    
	    if(temps_en_sec-temps_initial_en_secondes>temps_maximum){
		run_applet=true;break fin_de_programme; 
	    }
	    //if((dessiner_menu_principal_ou_fin&&(!creation_cylindress))||creation_cylindres)
	    if(toutdebut){
		dessiner_menu_principal_ou_fin=false;
		terminer_demo=false;
		if(demo_a_faire){
		    gr.drawImage(image2,20,450,this);
		    gr.drawImage(image,630,60,this);
		    n_ensembles=0;
		    for(int iii=1;iii>=0;iii--){
			creation_d_un_ensemble(iii,1,true);
			n_ensembles++;
		    }
		}
		vas_y=false;
		for(int ii=0;ii<n_ensembles;ii++)
		    vas_y|=ens_de_cyl[ii].du_nouveau;
		if(vas_y)
		    for(int ii=0;ii<n_ensembles;ii++)
			if(!ens_de_cyl[ii].en_train_de_peindre){
			    ens_de_cyl[ii].paint();
			    ens_de_cyl[ii].data.ecrit_donnees();
			    if(cliquee)
				break;
			}
		if(cliquee){
		    cliquee=false;
		    terminer_demo=true;
		}
		demo_a_faire=false;
		if(terminer_demo){
		    cliquee=false;pressee=false;
		    eraserect( gr,0,0,1000,1200,Color.white);
		    d_ou_je_reviens="je reviens de num_fen";	
		    eliminer(0);
		    eliminer(1);
		    n_ensembles=0;
		    Date maintenant=new Date();
		    temps_initial_en_secondes_prec=temps_en_secondes(maintenant);
		    toutdebut=false;
		    System.out.println(" on a detruit les fenetres de demo ");
		    ppmouseh=-100;ppmousev=-100;
		    menu_principal_et_fin();
		    dessiner_menu_principal_ou_fin=false;					    
		}
	    }else{
		if(dessiner_menu_principal_ou_fin)
		    menu_principal_et_fin();
		
		if(d_ou_je_reviens!=""){
		    System.out.println("d_ou_je_reviens "+d_ou_je_reviens+" n_ensembles "+n_ensembles);
		    n_ensembles=0;
		    setVisible(true);
		    if(dessiner_menu_principal_ou_fin)
			menu_principal_et_fin();
		}
		if((creation_cylindres)&&(!toutdebut))
		    demarrer_application();
		if(d_ou_je_reviens!=""){
		    System.out.println("***d_ou_je_reviens "+d_ou_je_reviens+" n_ensembles "+n_ensembles);
		    d_ou_je_reviens="";
		}
		if(n_ensembles!=0){
		    vas_y=false;
		    for(int ii=0;ii<n_ensembles;ii++)
			vas_y|=ens_de_cyl[ii].du_nouveau;
		    if(vas_y){
			dessiner_menu_principal_ou_fin=false;
			for(int ii=0;ii<n_ensembles;ii++){
			    if(!ens_de_cyl[ii].en_train_de_peindre){
				ens_de_cyl[ii].paint();
				ens_de_cyl[ii].data.ecrit_donnees();
				if(ens_de_cyl[ii].le_virer){
				    toutdebut=ens_de_cyl[ii].command=="Revenir a la page initiale avec infos";
				    if(toutdebut){
					demo_a_faire=true;
					terminer_demo=false;
					dessiner_menu_principal_ou_fin=false;
					creation_cylindres=false;
					eraserect(gr,0,0,1000,1200,Color.white);
				    }else
					dessiner_menu_principal_ou_fin=true;
				    creation_cylindres=true;
				    //dessiner_menu_principal_ou_fin=true;
				    relachee=false;
				    pressee=false;
				    draguee=false;
				    cliquee=false;
				    d_ou_je_reviens=ens_de_cyl[ii].command;
				    n_ensembles=0;				
				    eliminer(1-ii);
				    eliminer(ii);
				    break;				
				}
			    }
			    
			}
		    }
		}
	    }
	    i_run++;if(i_run==20)i_run=0;
	    //System.out.println("isleep");
	    try {Thread.sleep(isleep);}
	    catch (InterruptedException signal){System.out.println("catch ");}
	}
	for(int ii=0;ii<n_ensembles;ii++){
	    ens_de_cyl[ii].dispose();
	}
	dispose();System.exit(0); 
    }
    void demarrer_application(){ 
	//System.out.println("dem relachee "+relachee+" pressee "+pressee);
	if(cliquee){
	//if(relachee&&pressee){
	    cliquee=false;
	    System.out.println("11");
	    int xi=ppmouseh;int yi=ppmousev;
	    if ((xi > left_demarre)&&(xi < right_demarre+180))
		for(int i=0;i<2;i++)
		    if ((yi > top_demarre-200+i*30)&&(yi < top_demarre-200+(i+1)*30))
			ensemble_identiques=(i==0);
	    eraserect(gr,top_demarre-130,left_demarre,top_demarre-100,left_demarre+1000,Color.white);
	    gr.setColor(Color.blue);
	    gr.setFont(times_gras_24);
	    if(ensemble_identiques)
		gr.drawString("Les deux ensembles seront identiques, avec conducteur externe",left_demarre,top_demarre-110);
	    else
		gr.drawString("Les deux ensembles seront avec et sans conducteur externe",left_demarre,top_demarre-110);
	    if ((xi > left_demarre)&&(xi < right_demarre)){
		for(int i=0;i<4;i++){
		    if ((yi > top_demarre+i*20)&&(yi < top_demarre+(i+1)*20)){
			n_ensembles=2;
			for(int iii=1;iii>=0;iii--)
			    creation_d_un_ensemble(iii,i,false);
			relachee=false;pressee=false;
			creation_cylindres=false;
		    }
		}
	    }
	}		
    }
    public void creation_d_un_ensemble(int iii,int i_demarre,boolean demo){
	System.out.println(" creation_d_un_ensemble iii "+iii+" i_demarre "+i_demarre);
	if(iii==0&&!ensemble_identiques)
	    ens_de_cyl[iii]=new ensemble_de_cylindres("Conducteurs sans conducteur externe.",this,i_demarre,iii,demo);
	else
	    ens_de_cyl[iii]=new ensemble_de_cylindres("Conducteurs avec conducteur externe.",this,i_demarre,iii,demo);
    }
    void eliminer(int num_ens){
	if(ens_de_cyl[num_ens].command=="Sortir du programme")
	    run_applet=false;
	if(ens_de_cyl[num_ens].data!=null){
	    ens_de_cyl[num_ens].data.dispose();
	    ens_de_cyl[num_ens].data=null;
	}
	n_ensembles=0;
	cliquee=false;
	relachee=false;
	pressee=false;
	
	//ens_de_cyl[[num_ens].dispose();
	//ens_de_cyl[[num_ens]=null;
	
	ens_de_cyl[num_ens].dispose();
	ens_de_cyl[num_ens]=null;

	System.out.println("apres dispose() ");
    }
    public  void menu_principal_et_fin(){
	int ix,iy;
      if(run_applet){
	  gr.setColor(Color.red);gr.setFont(times14);
	  
	  System.out.println(" debut paint creation_cylindres "+creation_cylindres);
	  if(creation_cylindres){
	      gr.setFont(times_gras_24);
	      gr.setColor(Color.blue);
	      gr.drawString("Voulez vous deux ensembles initialement identiques,",left_demarre, top_demarre-260);	      
	      gr.drawString("pour pouvoir comparer apres modification ,",left_demarre, top_demarre-240);	      
	      gr.drawString("ou bien un ensemble avec conducteur externe et l'autre sans? ,",left_demarre, top_demarre-220);
	      paintrect_couleur(gr,top_demarre-200,left_demarre,top_demarre-140,right_demarre+180,Color.blue);
	      gr.drawString("Deux ensembles identiques, pour comparaison après modification",left_demarre+10, top_demarre-180);
	      drawline_couleur(gr,left_demarre, top_demarre-170,right_demarre+180,top_demarre-170,Color.blue);
	      gr.drawString("Deux ensemble avec conducteur externe, l'autre sans (par defaut)",left_demarre+10,top_demarre-150);

	      gr.setColor(Color.red);
	      gr.drawString("Cliquez dans ce menu pour choisir les deux ensembles ,",left_demarre, top_demarre-40);	      
	      gr.setFont(times14);
	      paintrect_couleur(gr,top_demarre,left_demarre,bottom_demarre,right_demarre,Color.red);
	      gr.setFont(times_gras_24);
	      gr.setColor(Color.black);
	      gr.setColor(Color.blue);gr.setFont(times14);
	      drawline_couleur(gr,left_demarre, top_demarre+20, right_demarre, top_demarre+20, Color.red);
	      drawline_couleur(gr,left_demarre, top_demarre+40, right_demarre, top_demarre+40, Color.red);
	      gr.drawString("Un fil plein , a l'interieur ou non d'un cylindre creux",left_demarre+10, top_demarre+14);//0
	      drawline_couleur(gr,left_demarre, top_demarre+60, right_demarre, top_demarre+60, Color.red);
	      gr.drawString("Deux fils pleins, a l'interieur ou non d'un cylindre creux",left_demarre+10, top_demarre+34);//0
	      drawline_couleur(gr,left_demarre, top_demarre+80, right_demarre, top_demarre+80, Color.red);
	      gr.drawString("Trois fils pleins, a l'interieur ou non d'un cylindre creux",left_demarre+10, top_demarre+54);//0
	      drawline_couleur(gr,left_demarre, top_demarre+100, right_demarre, top_demarre+100, Color.red);
	      gr.drawString("Quatre fils pleins, a l'interieur ou non d'un cylindre creux",left_demarre+10, top_demarre+74);//0
	      drawline_couleur(gr,left_demarre, top_demarre+120, right_demarre, top_demarre+120, Color.red);
	      drawline_couleur(gr,left_demarre,top_demarre+140, right_demarre, top_demarre+140, Color.red);
	      drawline_couleur(gr,left_demarre,top_demarre+160, right_demarre,top_demarre+160, Color.red);
	      drawline_couleur(gr,left_demarre,top_demarre+180, right_demarre,top_demarre+180, Color.red);
	      dessiner_menu_principal_ou_fin=false;
	  }
      }else{
	  eraserect(gr,0,0,1000,1200,Color.white);
	  gr.setFont(times_gras_24);gr.setColor(Color.black);
	  if(temps_en_sec-temps_initial_en_secondes>temps_maximum)
	      gr.drawString("TEMPS MAXIMUM EXPIRE",100,100);
	  else
	      gr.drawString("FIN DU PROGRAMME",100,100);
      }
    }
    void on_s_en_va(){
	for(int iii=1;iii>=0;iii--){
	    if(ens_de_cyl[iii]!=null){
		if(ens_de_cyl[iii].data!=null){
		    ens_de_cyl[iii].data.dispose();
		    ens_de_cyl[iii].data=null;
		}
		ens_de_cyl[iii].dispose();
		ens_de_cyl[iii]=null;
	    }
	}
	n_ensembles=0;
    }
    void drawline_couleur(Graphics g,int xin, int yin, int xfin, int yfin, Color couleur)
    {
	g.setColor(couleur);g.drawLine(xin,yin,xfin,yfin);
    }
    void paintrect_couleur(Graphics g,int top, int left, int bot, int right, Color couleur)
      
    {int x,y,width,height;
    x=left;y=top;width=right-left;height=bot-top;
    g.setColor(couleur);g.drawRect(x,y,width,height);
    }    
    void eraserect(Graphics g, int top, int left, int bot, int right,Color couleur){
	int x,y,width,height;
	x=left;y=top;width=right-left;height=bot-top;
	g.setColor(couleur);g.fillRect(x,y,width,height);
    }
    void paintcircle_couleur (Graphics g,long x,long y, long r,Color couleur)
    {
	g.setColor(couleur);g.fillOval((int)x,(int)y,(int)r,(int)r);
    }
    public void	traite_click(){
	//System.out.println("entree traite_click ");
	    Date maintenant=new Date();
	    temps_initial_en_secondes=temps_en_secondes(maintenant);
	toto_int=temps_initial_en_secondes_prec;
	if(cliquee){
	    if(temps_initial_en_secondes<temps_initial_en_secondes_prec+2){
		cliquee=false;pressee=false;relachee=false;
	    }else
		temps_initial_en_secondes_prec=temps_initial_en_secondes;
	}
	if(cliquee&&!toutdebut&&n_ensembles!=0){
	    cliquee=false;pressee=false;relachee=false;
	    for(int ik=0;ik<n_ensembles;ik++){    
		ens_de_cyl[ik].le_virer=true;
		ens_de_cyl[ik].command="Revenir a la fenetre principale";
	    }
	}
    }
    class MouseMotion extends MouseMotionAdapter{
	appli_nfils subject;
	public MouseMotion (appli_nfils a){
	    subject=a;
	}
	public void mouseMoved(MouseEvent e)
	{ppmouseh=e.getX();ppmousev=e.getY();draguee=false;}
	public void mouseDragged(MouseEvent e)
	{ppmouseh=e.getX();ppmousev=e.getY();draguee=true;
	//System.out.println("draguee dans Mousemove "+draguee);
	traite_click();
	}
    }
    class MouseStatic extends MouseAdapter
    {
	appli_nfils subject;
	public MouseStatic (appli_nfils a)
	{
	    subject=a;
	}
	public void mouseClicked(MouseEvent e)
	{
	    ppmouseh=e.getX();ppmousev=e.getY();
	    cliquee=true;
	    System.out.println("cliquee "+cliquee);
	    traite_click();
	    //	System.out.println("ens_de_cyl[icylindre].nb_el_ens "+ens_de_cyl[icylindre].nb_el_ens);
	    //System.out.println("icylindre "+icylindre);
	    //for( int iq=0;iq<ens_de_cyl[icylindre].nb_el_ens;iq++)
	}
	public void mousePressed(MouseEvent e)
	{
	    ppmouseh=e.getX();ppmousev=e.getY();pressee=true;
	    System.out.println("pressee "+pressee);
	    traite_click();
	}
	public void mouseReleased(MouseEvent e)
	{
	    ppmouseh=e.getX();ppmousev=e.getY();cliquee=true;relachee=true;
	    System.out.println("relachee "+relachee);
	}
    }
}
class ensemble_de_cylindres extends Frame implements ActionListener{
    static int top_ens_cyl = 0,left_ens_cyl = 0,bottom_ens_cyl = 610,right_ens_cyl = 600;
    static final float pi=(float)3.141592652;
    vecteur vct;
    float deux_pi_eps0=0;
    float dV[][][]=new float[5][5][63];
    float dV_moyen[][]=new float[5][5];
    float q_en_regard[][]=new float[5][5];
    float dV2_moyen[][]=new float[5][5];
    int n_valeurs_dV[][]=new int[5][5];
    int numeros_kk[][][]=new int[5][5][63];
    String nom_conduc[]=new String[5];	MenuItem item_fil[]= new MenuItem[5];
    String issu_de_fil[]=new String[5];
    boolean zeichnen_punkt=false;
    String str_increm; 
    float kk_plus_precis=0;boolean booboo=false;
    boolean diff_signes=false;
    boolean calcul_des_q_en_regard[]=new boolean[5];
    biparam parametres_droite_initiale,parametres_droite_finale;float distance_avant_fin=0;
    boolean hors_cadre=false,a_reculon=false;float prd_vect=0,prd_vect1=0;
    boolean demo,ligne_coupee=false,ligne_coupee_d_emblee=false,le_virer=false;;
    point pt_essai,pt_d_intersec,dist_pt_init,dist_pt_fin,cchhpp,pt_ss;
    float angle_champs=0,angle_positions=0,diff_angles=0,diff_angles0=0,diff_angles_prec=0,increment_angles=(float)0.1;
    float somme_inc=0;
    vecteur chp_dpt,chp_dpt_prec;
    int iq_arrivee=0,iq_arrivee_prec=0, numero_zone_arrivee=0;
    boolean calculs_faits=false,lignes_de_champ_faites=false,calculating=false;
    int iter,nfiltot,num_secteur=0;
    biboolean bibo;
    float distance_quadrillage=(float)12.;
    float champ_de_ref=0;
    boolean du_nouveau,en_train_de_peindre;
    donnees data;
    boolean ret,trouve_deplacement;boolean attend_souris=false;
    float x_essai=0,y_essai=0,dtet,dttt;
    float dx_pt_souris, dy_pt_souris;
    vecteur chp_pts_a_montrer[]=new vecteur[10000];
    int fil_initial_de_la_ligne[]=new int[10000];
    int fil_initial_ligne_a_montrer=0;boolean montrer_un_champ_sur_2=false;
    boolean draw_point=false;
    boolean champ_du_point_a_dessiner[]=new boolean[10000];
    vecteur vovo,wowo,wawa;
    point difference_pt,difference_chp;
    int nombre_de_pts_a_montrer=0,nombre_de_pts_a_montrer_init=0;
    point toto,titi,tata,zer;point dist_reelle;float diff_potentiel=0;
    int ppmouseh,ppmousev,ppmouseh_prec,ppmousev_prec;
    float echelle=(float)0.1;boolean relachee,pressee,cliquee,draguee;
    cylindre cyl[]=new cylindre [10];
    String command,comment,comment_init;
    point champ[]=new point [64];
    boolean dejaext[]=new boolean[10];	boolean fin_gere_menus_souris;
    int  numeroens[]=new int [10];int i_demarre,n_stoppages;
    int fact_zoom_suppl;float fct_zm_sspl=(float)1.;
    point elec_centre_q_totales,elec_centre_de_plus;
    Graphics gr_ensemble;
    appli_nfils subject;int iq_dep=-1;
    private MouseStatic mm;    private MouseMotion motion;
    boolean conseil,cree_wires,zoom;int i_ens;
    MenuBar mb1[]=new MenuBar[2];
    public ensemble_de_cylindres(String s,appli_nfils a,int i_demarre1,int i_ens1,boolean demo1){	
	super(s);
	subject=a;i_ens=i_ens1;i_demarre=i_demarre1;du_nouveau=true;
	cree_wires=false;n_stoppages=0;
	deux_pi_eps0=2*subject.pi*subject.eps0;
	en_train_de_peindre=false;calculating=false;
	demo=demo1;
	zer=new point((float)0.,(float)0.);
	if(demo){
	    bottom_ens_cyl = 630/2;right_ens_cyl = 600/2;
	}else{
	    bottom_ens_cyl = 630;right_ens_cyl = 600;
	}
	elec_centre_q_totales=new point(zer);
	elec_centre_de_plus=new point(zer);
	toto=new point(zer);
	titi=new point(zer);
	tata=new point(zer);
	dist_reelle=new point(zer);
	vovo=new vecteur(zer,zer);
	wowo=new vecteur(zer,zer);
	wawa=new vecteur(zer,zer);
	vct=new vecteur(zer,zer);
	bibo =new biboolean(false,false);
	difference_pt=new point(zer);difference_chp=new point(zer);
	pt_essai=new point(zer);
	pt_ss=new point(zer);
	chp_dpt_prec=new vecteur(zer,zer);
	chp_dpt=new vecteur(zer,zer);
	parametres_droite_initiale =new biparam((float)0.,(float)0.);
	parametres_droite_finale=new biparam((float)0.,(float)0.);
	pt_d_intersec=new point(zer);
	dist_pt_init=new point(zer);
	dist_pt_fin=new point(zer);
	dtet=2*subject.pi/((float)4.0*subject.dim);
        dttt=dtet/(float)10.0;
	nom_conduc[0]="A";nom_conduc[1]="B";nom_conduc[2]="C";nom_conduc[3]="D";nom_conduc[4]="E";
	issu_de_fil[0]="montrer les champs issus du fil A";
	issu_de_fil[1]="montrer les champs issus du fil B";
	issu_de_fil[2]="montrer les champs issus du fil C";
	issu_de_fil[3]="montrer les champs issus du fil D";
	issu_de_fil[4]="montrer les champs issus du fil E";	
	cchhpp=new point(zer);
        addWindowListener(new java.awt.event.WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent e) {
		    le_virer=true;du_nouveau=true;
		    command="Revenir a la page principale";
		};
	    });

        fact_zoom_suppl=1;
	trouve_deplacement=false;comment="";
 	mm=new MouseStatic(this);
	this.addMouseListener(mm);
	motion=new MouseMotion(this); setBackground(Color.white);
	this.addMouseMotionListener(motion);
	setLayout(new FlowLayout());
	cree_fils();
	mb1[i_ens]=null;barre_des_menus();
	pack();
	setVisible(true);
	gr_ensemble=getGraphics();
	gr_ensemble.setColor(Color.cyan);
	setSize(right_ens_cyl-left_ens_cyl,bottom_ens_cyl-top_ens_cyl);
	if(demo){
	    setLocation(left_ens_cyl+i_ens*600/2,top_ens_cyl);
	}else{
	    setLocation(left_ens_cyl+i_ens*600,top_ens_cyl);
	}
	    comment_init="";
	data=new donnees("Donnees");
	//paint();
	calculs();
	//	paint();
	lignes_de_champ();
	paint();
    }
    public void barre_des_menus(){
	System.out.println("i_demarre  "+i_demarre);
	mb1[i_ens]=new MenuBar();
	if(!demo){
	    Menu sortir= new Menu("Sortir");
	    MenuItem iteb1=new MenuItem("Revenir a la page principale");
	    sortir.add(iteb1);
	    iteb1.addActionListener(this);
	    MenuItem iteb11=new MenuItem("Revenir a la page initiale avec infos");
	    sortir.add(iteb11);
	    iteb11.addActionListener(this);
	    MenuItem iteb12=new MenuItem("Sortir du programme");
	    sortir.add(iteb12);iteb12.addActionListener(this);
	    mb1[i_ens].add(sortir);
	}
	Menu dilater= new Menu("Montrer les champs");
	for(int i=0;i<nfiltot;i++){
	    item_fil[i]=new MenuItem(issu_de_fil[i]);
	    dilater.add(item_fil[i]);
	    item_fil[i].addActionListener(this);
	}
	MenuItem item1_sur_2=new MenuItem("ne montrer qu'un champ sur 2");
	MenuItem item11=new MenuItem("multiplier l'echelle d'un facteur 2");
	MenuItem item2=new MenuItem("diviser l'echelle d'un facteur 2");
	MenuItem item3=new MenuItem("utiliser l'echelle de l'autre fenetre");
	dilater.add(item1_sur_2);
	item1_sur_2.addActionListener(this);
	dilater.add(item11);
	item11.addActionListener(this);
	dilater.add(item2);
	item2.addActionListener(this);
	dilater.add(item3);
	item3.addActionListener(this);
	mb1[i_ens].add(dilater);
	if(demo){
	    item11.setEnabled(false);
	    item2.setEnabled(false);
	    item3.setEnabled(false);
	}  
	Menu operations_sur_elements= new Menu("modifier l'ensemble");
	if(demo)
	    operations_sur_elements= new Menu("modifier");
	MenuItem itepq3=new MenuItem("deplacer un cylindre par son centre");
	MenuItem iterr=new MenuItem("augmenter de 10 nCb/m la charge d'un fil");
	MenuItem itepp=new MenuItem("diminuer de 10 nCb/m la charge d'un fil");
	MenuItem iterr1=new MenuItem("augmenter de 20% le rayon d'un cylindre");
	MenuItem itepp1=new MenuItem("diminuer de 20% le rayon d'un cylindre");
	operations_sur_elements.add(itepq3);
	itepq3.addActionListener(this);
	operations_sur_elements.add(iterr);
	iterr.addActionListener(this);
	operations_sur_elements.add(itepp);
	itepp.addActionListener(this);
	operations_sur_elements.add(iterr1);
	iterr1.addActionListener(this);
	operations_sur_elements.add(itepp1);
	itepp1.addActionListener(this);
	mb1[i_ens].add(operations_sur_elements);
	if(demo){
	    itepq3.setEnabled(false);
	    iterr.setEnabled(false);
	    itepp.setEnabled(false);
	    iterr1.setEnabled(false);
	    itepp1.setEnabled(false);
	}  
	setMenuBar(mb1[i_ens]);
    }
    public void actionPerformed(ActionEvent e){
	command=e.getActionCommand();
	System.out.println("i_ens "+i_ens+" n_stoppages "+n_stoppages+" command "+command);
	//if(command!="")n_stoppages=0;
	if(command!=""){
	    Date maintenant=new Date();
	    subject.temps_initial_en_secondes=subject.temps_en_secondes(maintenant);
	    if (command=="Revenir a la page principale"||command=="Sortir du programme"||command=="Revenir a la page initiale avec infos"){
		le_virer=true;
	    }
	    if(command=="Sortir du programme")
		subject.run_applet=false;	    
	}
	if(command!="")
	    traite_commande ();
    }
    public void traite_commande (){
	boolean dg=false;
	if(command!=""){
	    fin_gere_menus_souris=false;
	    du_nouveau=true;
	    //System.out.println(" command "+command);
	}
	if(command=="deplacer un cylindre par son centre"){
	    comment="Faites glisser le point rouge du centre du fil choisi";
	    attend_souris=true;
	}
	if(command=="ne montrer qu'un champ sur 2"){
	    montrer_un_champ_sur_2=!montrer_un_champ_sur_2;
	    System.out.println(" montrer_un_champ_sur_2 "+montrer_un_champ_sur_2);   
	}
	if(command=="augmenter de 10 nCb/m la charge d'un fil"||command=="diminuer de 10 nCb/m la charge d'un fil"||command=="augmenter de 20% le rayon d'un cylindre"||command=="diminuer de 20% le rayon d'un cylindre"){
	    comment="Cliquez sur le centre du cylindre que vous choisissez.";attend_souris=true;
	}
	if(attend_souris){
	    gr_ensemble.setColor(Color.black);
	    gr_ensemble.drawString(comment,100,200);
	}
	if (command=="utiliser l'echelle de l'autre fenetre"){
	    if(subject.n_ensembles==2){
		System.out.println(" i_ens "+i_ens+" echelle "+echelle+" fact_zoom_suppl "+fact_zoom_suppl+" fct_zm_sspl "+fct_zm_sspl);
		echelle=subject.ens_de_cyl[1-i_ens].echelle;
		fact_zoom_suppl=subject.ens_de_cyl[1-i_ens].fact_zoom_suppl;
		fct_zm_sspl=subject.ens_de_cyl[1-i_ens].fct_zm_sspl;
		System.out.println("echelle "+echelle+" fact_zoom_suppl "+fact_zoom_suppl+" fct_zm_sspl "+fct_zm_sspl);
	    }
	}
	System.out.println("montrer_un_champ_sur_2 dans traite_commande() "+montrer_un_champ_sur_2);
	for(int i=0;i<nfiltot;i++){
	    if(command==issu_de_fil[i]){
		fil_initial_ligne_a_montrer=i;
		System.out.println(" fil_initial_ligne_a_montrer "+fil_initial_ligne_a_montrer);
		break;
	    }
	}
	int toto_int=0;
	for (int j=0;j<nombre_de_pts_a_montrer;j++){
	    champ_du_point_a_dessiner[j]=(fil_initial_de_la_ligne[j]==fil_initial_ligne_a_montrer)&&!(montrer_un_champ_sur_2&&j/2*2==j);
	    if(champ_du_point_a_dessiner[j])
		toto_int++;
	}
	System.out.println(" toto_int     "+ toto_int);
	if (command=="multiplier l'echelle d'un facteur 2"){
	    fact_zoom_suppl++;dg= true;
	}
	if (command=="diviser l'echelle d'un facteur 2"){
	    fact_zoom_suppl--;dg= true;
	}
	if(dg){
	    command="";
	    attend_souris=false;
	    fct_zm_sspl=(float)1.0;
	    if (fact_zoom_suppl > 1)
		for(int i=2;i<=fact_zoom_suppl;i++)
		    fct_zm_sspl*= 2;
	    if (fact_zoom_suppl < 1)
		for(int i=fact_zoom_suppl;i<1;i++)
		    fct_zm_sspl/=(float)2.0;
	    System.out.println("fct_zm_sspl "+ fct_zm_sspl);
	}
    }
    public boolean gere_menus_souris (){
	if(command!="")fin_gere_menus_souris=false;
	if(command=="deplacer un cylindre par son centre"){
	    ret=false;
	    if(draguee||trouve_deplacement)
		ret=glisser_vibr();
	    if(relachee&&trouve_deplacement)
		ret=glisser_vibr();
	    if(pressee)
	    	ret=glisser_vibr();
	}
	boolean clic_ok=false;int iq_choisi=-1;
	if(attend_souris){
	    if(cliquee){
		if(!((ppmouseh==ppmouseh_prec)&&(ppmousev==ppmousev_prec))){
		    ppmouseh_prec=ppmouseh;ppmousev_prec=ppmousev;
		    clic_ok=true;
		    for(int iq=0;iq<nfiltot;iq++){
			if((Math.abs(ppmouseh-cyl[iq].xc)<7)&&(Math.abs(ppmousev-cyl[iq].yc)<7))
			    iq_choisi=iq;
		    }
		}	    
	    }
	}
	if(clic_ok){
	    boolean impossible=false;
	    if(iq_choisi!=-1){
		if(command=="augmenter de 10 nCb/m la charge d'un fil"){
		    if(cyl[iq_choisi].fil_ext){
			comment="Impossible, c'est le cylindre exterieur";
			impossible=true;
		    }else{
			cyl[iq_choisi].q+=(float)10.;	
			for(int iq=0;iq<nfiltot;iq++)
			    if(cyl[iq].fil_ext)
				cyl[iq].q-=(float)10.;	
		    }
		}
		if(command=="diminuer de 10 nCb/m la charge d'un fil"){
		    if(cyl[iq_choisi].fil_ext){
			comment="Impossible, c'est le cylindre exterieur";
			impossible=true;
		    }else{
			cyl[iq_choisi].q-=(float)10.;	
			for(int iq=0;iq<nfiltot;iq++)
			    if(cyl[iq].fil_ext)
				cyl[iq].q+=(float)10.;	
		    }
		}
		if((command=="augmenter de 20% le rayon d'un cylindre")||(command=="diminuer de 20% le rayon d'un cylindre")){
		    float aaa=0;
		    if(command=="augmenter de 20% le rayon d'un cylindre")
			aaa=cyl[iq_choisi].rc*(float)1.2;
		    else
			aaa=cyl[iq_choisi].rc*(float)0.8;
		    for(int iq=0;iq<nfiltot;iq++){
			float dist=(float)Math.sqrt((float)Math.pow(cyl[iq].xc-cyl[iq_choisi].xc,2)+(float)Math.pow(cyl[iq].yc-cyl[iq_choisi].yc,2));
			//boolean exterieur=(dist>cyl[iq].rc)&&(dist>cyl[iq_choisi].rc));
			if(!((dist>cyl[iq].rc+aaa)||(dist<Math.abs(cyl[iq].rc-aaa)))){
			    comment="Impossible, deux cercles se coupent";
			    impossible=true;
			}
			System.out.println("impossible "+impossible+" dist "+dist+" cyl[iq].rc "+cyl[iq].rc+" aaa "+aaa);
			
		    }
		    if(!impossible)
			cyl[iq_choisi].rc=aaa; 
		}
	    }
	    if(!impossible){
		calculs();
		lignes_de_champ();
	    }
	}
	return fin_gere_menus_souris;
    }
    public void paint(){
	//if(attend_souris)
	//return;
	en_train_de_peindre=true;
	Color defond=Color.white;
	du_nouveau=false;
	cliquee=false;
	for (int iq=0;iq<nfiltot;iq++)
	    if(cyl[iq].fil_ext)
		defond=subject.cuivre;   
	subject.eraserect(gr_ensemble,0, 0, 1000, 1200,defond);
	for (int iq=0;iq<nfiltot;iq++)
	    if(cyl[iq].fil_ext)subject.paintcircle_couleur(gr_ensemble,Math.round(cyl[iq].xc-cyl[iq].rc),Math.round(cyl[iq].yc-cyl[iq].rc), 2*Math.round(cyl[iq].rc+1),Color.white);
	System.out.println("paint trouve_deplacement "+trouve_deplacement);
	for (int iq=0;iq<nfiltot;iq++){
	    //System.out.println(iq+ " iq "+" cyl[iq].xc "+cyl[iq].xc+" cyl[iq].yc "+cyl[iq].yc+" cyl[iq].rc "+cyl[iq].rc);
	    if(!cyl[iq].fil_ext)
		subject.paintcircle_couleur(gr_ensemble,Math.round(cyl[iq].xc-cyl[iq].rc),Math.round(cyl[iq].yc-cyl[iq].rc), 2*Math.round(cyl[iq].rc+1),subject.cuivre);
	    subject.paintcircle_couleur(gr_ensemble,Math.round(cyl[iq].xc),Math.round(cyl[iq].yc),6,Color.red);
	}
	if(!attend_souris){
	    if(calculs_faits){
		if(!trouve_deplacement)
		    for(int iq=0;iq<nfiltot;iq++)
			cyl[iq].chp_clc.va_dessiner();		    
	    }
	    if(lignes_de_champ_faites){
		for (int i=0;i<nombre_de_pts_a_montrer;i++)
		    if(champ_du_point_a_dessiner[i])
			chp_pts_a_montrer[i].dessine(echelle,fct_zm_sspl,gr_ensemble,Color.green);
		System.out.println("nombre_de_pts_a_montrer "+nombre_de_pts_a_montrer);
		gr_ensemble.setColor(Color.black);
		gr_ensemble.drawString(comment_init,220,bottom_ens_cyl-top_ens_cyl-28);
		data.ecrit_donnees();
		System.out.println("apres data.ecrit"+nombre_de_pts_a_montrer);
	    }
	}else{
	    gr_ensemble.setColor(Color.black);
	    gr_ensemble.drawString(comment,100,300);
	}
	gr_ensemble.setColor(Color.yellow);gr_ensemble.setFont(subject.times_gras_14);
	for (int iq=0;iq<nfiltot;iq++){
	    String str=cyl[iq].q+"nCb/m";
	    if(!cyl[iq].fil_ext){
		gr_ensemble.drawString(str,(int)Math.round(cyl[iq].xc)-35,(int)Math.round(cyl[iq].yc)+20);
		gr_ensemble.drawString(nom_conduc[iq],(int)Math.round(cyl[iq].xc),(int)Math.round(cyl[iq].yc-10));
	    }else{
		gr_ensemble.drawString(str,right_ens_cyl-80,top_ens_cyl+80);
		gr_ensemble.drawString(nom_conduc[iq],right_ens_cyl-60,top_ens_cyl+80-20);
	    }
	}	
	en_train_de_peindre=false;
    }
    public boolean glisser_vibr ()
    {
      boolean ret=false;
      int xi=ppmouseh;int yi=ppmousev;
      //System.out.println("xi "+xi+" yi"+yi+" pressee "+pressee+"trouve_deplacement"+trouve_deplacement);
      if (pressee){
	  if(!trouve_deplacement){
	      for(int iq=0;iq<nfiltot;iq++){
		  System.out.println("iq cherche "+iq);
		  float ddx=Math.abs(ppmouseh-cyl[iq].xc);
		  float ddy=Math.abs(ppmousev-cyl[iq].yc);
		  if ((ddx < 10 )&&(ddy <10)){
		      comment="iq trouve "+iq;
		      trouve_deplacement=true;
		      ret=true;
		      iq_dep=iq;
		      dx_pt_souris = ppmouseh-cyl[iq].xc;
		      dy_pt_souris = ppmousev-cyl[iq].yc;
		      System.out.println(" *****************deplacement initial, iq "+iq);
		  }
	      }
	  }
      }
      if(trouve_deplacement){
	  if(relachee){
	      //System.out.println("&&&&&&&&&&&&&&&&&&&&&& "+iq_dep);
	      System.out.println("trouve_deplacement "+trouve_deplacement+" relachee "+relachee+" iq_dep "+iq_dep);
	      deplacement(iq_dep);
	      trouve_deplacement=false;iq_dep=-1;
	      if(cree_wires){
		  calculs();
		  lignes_de_champ();
	      }
	      relachee=false;
	  }else{
	      if(draguee){
		  System.out.println("trouve_deplacement "+trouve_deplacement+" draguee "+draguee);
		  deplacement(iq_dep);
	      }
	  }
      }
      return ret;
    }
    public void deplacement( int iq)
    {
	if(relachee||draguee){
	    int ay = (int) Math.round(ppmousev - dy_pt_souris);
	    //cyl[iq].yc = ay;
	    int ax = (int) Math.round(ppmouseh - dx_pt_souris);
	    //cyl[iq].xc = ax;
	    boolean f=cyl[iq]. fil_ext;
	    int rrr=(int)cyl[iq].rc;
	    int q1=(int)cyl[iq].q;
	    cyl[iq]=null;
	    cyl[iq]=new cylindre(ax,ay, rrr,q1,f);
	}
    }
 
    public void champ_centre_cylindre( int iq )
    {
 	elec_centre_q_totales.zero();elec_centre_de_plus.zero();
	for(int iq_prime=0;iq_prime<nfiltot;iq_prime++){
	    if((iq!=iq_prime)&&!(cyl[iq_prime].fil_ext)){
		point dist=new point(cyl[iq].xc-cyl[iq_prime].xc,cyl[iq].yc-cyl[iq_prime].yc);
		elec_centre_q_totales.additionne_facteur(dist,cyl[iq_prime].q/(dist.longueur_carre()*deux_pi_eps0));
	    }
	}
	for(int iq_prime=0;iq_prime<nfiltot;iq_prime++){
	    for (int k=0;k<subject.dim;k++){
		float cc=subject.cosinus[k];
		float ss=subject.sinus[k];
		if(iq==iq_prime){
		    point vec_un=new point(-cc,-ss);
		    //float sig=subject.eps0*cyl[iq].chp_elec_radial_surf[k];
		    elec_centre_de_plus.additionne_facteur(vec_un,cyl[iq].sigma[k]*(2*subject.pi/subject.dim)/(2*subject.pi*subject.eps0));
		}else{
		    float xp=cyl[iq].xc;float yp=cyl[iq].yc;
		    point dist=new point(xp-(cyl[iq_prime].xc+cyl[iq_prime].rc*cc),yp-(cyl[iq_prime].yc+cyl[iq_prime].rc*ss));
		    float d=cyl[iq_prime].rc*(2*subject.pi/subject.dim);
		    elec_centre_de_plus.additionne_facteur(dist,cyl[iq_prime].sigma[k]*d/(dist.longueur_carre()*deux_pi_eps0));
		}
	    }
	}
	//if((iq==0)&&(iter==0))elec.print("iter "+iter+" elec ");
	//elec_centre_q_totales.print(" iq "+iq+" elec_centre_q_totales ");
	//elec_centre_de_plus.print(" iq "+iq+" elec_centre_de_plus ");
	return;
    }
    public point champ_pendant_calculs( int iq, int k, boolean recherche_angle)
    {
	point elec=new point(zer);
	//if(recherche_angle){
	//c=subject.cosinus[k];s=subject.sinus[k];
	//}
	//else{
	//c=cos_angle[k];s=sin_angle[k];
	//}
	float c,s;
	if(recherche_angle){
	    c=subject.cosinus[k];s=subject.sinus[k];
	}else{
	    c=cyl[iq].cos_angle[k];s=cyl[iq].sin_angle[k];
	}
	//float c=(float)Math.cos(k*2.0*subject.pi/subject.dim+cyl[iq].angle);
	//float s=(float)Math.sin(k*2.0*subject.pi/subject.dim+cyl[iq].angle);
	point vec_un=new point(c,s);
	float xp=cyl[iq].xc+cyl[iq].rc*c;
	float yp=cyl[iq].yc+cyl[iq].rc*s;
	//dist.print(" iq "+iq+" k "+k+" sigma_suppl[k] "+cyl[iq].sigma_suppl[k]+" dist ");	
	//elec_centre_de_plus.print(" elec_centre_de_plus ");
	point elecpr=new point(zer);
	for(int iq_prime=0;iq_prime<nfiltot;iq_prime++){
	    //if((iq==0)&&recherche_angle)System.out.println("iq "+iq+" iq_prime "+iq_prime+" k "+k);
	    if((iq!=iq_prime)&&(iter==0)&&!cyl[iq_prime].fil_ext){
		point dist=new point(xp-cyl[iq_prime].xc,yp-cyl[iq_prime].yc);
		float facteur=cyl[iq_prime].q/(dist.longueur_carre()*deux_pi_eps0);
		elec.additionne_facteur(dist,facteur);//%%%%
		//elec.additionne_facteur(dist,facteur);
		//if((k==0)&&(iq==0))System.out.println("iq "+iq+" c "+c+" s "+s+" facteur "+facteur);
		if(iq==0){
		    //elecpr.assigne(elec);
		    //elecpr.projections(c,s);
		    //elecpr.print(" iq_prime "+iq_prime+" k "+k+"*elecpr ");
		    //point el=new point(zer);
		    //el.additionne_facteur(dist,facteur);
		    //el.projections(c,s);
		    //el.print(" iq_prime "+iq_prime+" k "+k+"*el ");
		}
	    }
	    //if((iq==0)&&(iter==1))elec.print(" k "+k+" iq_prime "+iq_prime+" elec ");
	    //
	    
	    //if((iter!=0)&&(!((iter==1)&&(iq==iq_prime))))//%%%%auquel cas le champ est compense par le champ des charges totales
	    float d=cyl[iq_prime].rc*(2*subject.pi/subject.dim/deux_pi_eps0);
	    if((iter!=0)&&(iq!=iq_prime)){
		for (int kk=0;kk<subject.dim;kk++){
		    tata.assigne(xp-(cyl[iq_prime].xc+cyl[iq_prime].rc*subject.cosinus[kk]),yp-(cyl[iq_prime].yc+cyl[iq_prime].rc*subject.sinus[kk]));
		    elec.additionne_facteur(tata,cyl[iq_prime].sigma_suppl[kk]*d/(tata.longueur_carre()));
		}
	    }
	}
	elec.projections(c,s);
	//point elecsec=new point(elec.x,elec.y);
	//elecsec.soustrait(elecpr);
	//if(iq==0)elecsec.print(" elecsec ");
	//if(iq==0)elec.print(" k "+k+"*** elec ");
	return elec;
    }
    public point champ( point rp){
	point elec=new point(zer);
	for(int iq=0;iq<nfiltot;iq++){
	    point dist=new point(zer); dist.assigne_soustrait(rp,cyl[iq].centre);
	    //point dist=new point(xp-cyl[iq].xc,yp-cyl[iq].yc);
	    if(dist.longueur_carre()>(float)Math.pow(cyl[iq].rc,2)){
		float facteur=cyl[iq].q/(dist.longueur_carre()*deux_pi_eps0);
		elec.additionne_facteur(dist,facteur);
	    }
	    float th=-dtet/(float)2.;
	    point dista=new point(zer);point distance_au_centre =new point(zer);
	    //float c1=0,s1=0;
	    float sig=0;
	    distance_au_centre.assigne_soustrait(rp,cyl[iq].centre);
	    for (int kk=0;kk<4*subject.dim;kk++){
		th+=dtet;
		dista.assigne_soustrait(distance_au_centre,cyl[iq].point_surface[kk]);
		//point dista=new point(xp-cyl[iq].xc-cyl[iq].rc*(float)Math.cos(th),yp-cyl[iq].yc-cyl[iq].rc*(float)Math.sin(th));
		/*
		sig=0;
		for (int jj=0;jj<8;jj++){
		    c1=(float)Math.cos((jj+1)*th);s1=(float)Math.sin((jj+1)*th);
		    sig+=(cyl[iq].sigma_cos[jj]*c1+cyl[iq].sigma_sin[jj]*s1);
		}
		*/
		int k=kk/4;
		int kb=kk-k*4;
		//System.out.println(" iq "+iq+" k "+k+" sig "+sig+" cyl[iq].sigma "+cyl[iq].sigma[k]);
		if(k>=2){
		    int kp1=k+1;
		    if(kp1>=subject.dim)kp1=0;
		    sig=(cyl[iq].sigma[k]*((float)5.5-kb)+cyl[iq].sigma[kp1]*(kb-(float)1.5))/(float)4.;//%%%%
		}else{
		    int km1=k-1;
		    if(km1<0)km1=subject.dim-1;
		    sig=(cyl[iq].sigma[k]*(kb+(float)2.5)+cyl[iq].sigma[km1]*((float)1.5-kb))/(float)4.;//%%%%
		}
		
		//float distc=(float)Math.sqrt((float)Math.pow(xp-cyl[iq].xc,2)+(float)Math.pow(yp-cyl[iq].yc,2));
		if ((Math.abs(distance_au_centre.longueur()-cyl[iq].rc) < 5.0)&&(dista.longueur_carre() < (float)Math.pow((float)20.0,2))) {
		    //float dttt=dtet/10.0;
		    float ttt=th-dtet/(float)2.0-dttt/(float)2.0;
		    for (int ittt=0;ittt<10;ittt++){
			ttt=ttt+dttt;
			toto.assigne(cyl[iq].rc*(float)Math.cos(ttt),cyl[iq].rc*(float)Math.sin(ttt));
			titi.assigne_soustrait(distance_au_centre,toto);
			elec.additionne_facteur(titi,sig*cyl[iq].rc*dttt/(titi.longueur_carre()*deux_pi_eps0));//%%%%
		    }
		}else//%%%%
		    elec.additionne_facteur(dista,sig*cyl[iq].rc*dtet/(dista.longueur_carre()*deux_pi_eps0));//%%%%
	    }
	}
	//System.out.println(" echelle "+echelle);
	return elec;
    }
    public vecteur champ_suivant(vecteur chp_initial,float dist,int num_conduc_depart,int ndc_lgn,int essai){
	//if(i_ens==0)
	//  chp_initial.print(" chp_initial ");
	//System.out.println(" ligne_coupee "+ligne_coupee+" ligne_coupee_d_emblee "+ligne_coupee_d_emblee+" ndc_lgn "+ndc_lgn);
	ligne_coupee=false;ligne_coupee_d_emblee=false;
	if(essai==1)
	    chp_dpt.pnt.assigne_facteur(chp_initial.chp,dist/chp_initial.chp.longueur());
	else
	    chp_dpt.pnt.assigne_facteur(chp_initial.chp,dist/4/chp_initial.chp.longueur());
	pt_essai.assigne_additionne(chp_initial.pnt,chp_dpt.pnt);
        bibo=point_final_de_la_ligne(pt_essai,dist);
	if(ndc_lgn>(float)1.&&bibo.pt_fn_ligne)
	    return chp_initial;// n'importe quel champ, la ligne est teminee
	if(ndc_lgn<=1||ndc_lgn>1&&bibo.approche_fin_ligne){
	    //if(ndc_lgn>1)
	    //System.out.println(" num_secteur "+num_secteur+" ndc_lgn "+ndc_lgn+" bibo.approche_fin_ligne "+bibo.approche_fin_ligne);
	    parametres_droite_initiale.assigne(parametres_droite_perp_au_champ(chp_initial));
	    //chp_initial.chp.print(" chp_initial.chp ");
	    //pt_essai.print(" pt_essai ");
	    //toto.assigne_soustrait(pt_essai,chp_initial.pnt);
	    return champ_suivant_par_intersections(chp_initial,dist,num_conduc_depart);
	}else{
	    boolean prprint=false;
	    chp_dpt.chp.assigne(champ(pt_essai));
	    diff_angles0=d_angle_chp_pnt(toto,true,chp_initial);
	    if(Math.abs(angle_champs)>(float)1./essai){
		ligne_coupee_d_emblee=true;
		System.out.println(" ligne_coupee_d_emblee "+" angle_champs "+angle_champs);
		return wowo;
	    }
	    ///*
	    if(num_conduc_depart==0&&prprint){
		pt_essai.print("0 num_secteur "+num_secteur+" ndc_lgn "+ndc_lgn+" diff_angles0 "+(float)diff_angles0+" angle_champs "+angle_champs+" angle_positions "+angle_positions+" essai "+essai+" dist "+(float)dist+" pt_essai ");
		if(Math.abs(diff_angles0)>(float)1.5)
		    cyl[1000]=null;
	    }
	    //*/
	     if(Math.abs(diff_angles0)<(float)0.01){
		 vovo.assigne(chp_dpt.chp,pt_essai);
		 return vovo;
	     }

	    float inc=increment_angles;
	    somme_inc=0;
	    chp_dpt_prec.pnt.assigne(chp_dpt.pnt);
	    diff_signes=incremente_et_compare(inc,chp_initial);
	    boolean se_rapproche_de_zero=Math.abs(diff_angles)<Math.abs(diff_angles0);
	    diff_angles_prec=diff_angles;
	    inc*=(float)0.999;//pour eviter de revenir exactement au meme point en quelques coups
	    if(diff_signes||!diff_signes&&!se_rapproche_de_zero)
		inc=-inc;
	    //System.out.println("deb  "+" inc "+inc+" diff_angles0 "+(float)diff_angles0+" diff_angles "+(float)diff_angles+" diff_signes "+ diff_signes+" se_rapproche_de_zero "+se_rapproche_de_zero);
	    if(num_conduc_depart==0){
		if(prprint){
		    System.out.println("0 num_secteur "+num_secteur+" ndc_lgn "+ndc_lgn+" diff_angles "+(float)diff_angles+" inc "+(float)inc+" angle_champs "+angle_champs+" angle_positions "+angle_positions+" se_rapproche_de_zero "+se_rapproche_de_zero);
		chp_dpt_prec.pnt.print(" chp_dpt_prec.pnt ");
		}//else
		//System.out.println("0 num_secteur "+num_secteur+" ndc_lgn "+ndc_lgn+" kk_plus_precis "+(float)kk_plus_precis);
		//chp_dpt.pnt.print(" chp_dpt.pnt ");
		//chp_dpt.chp.print(" chp_dpt.chp ");
		if(Math.abs(diff_angles)>(float)1.5&&prprint){
		    chp_dpt.pnt.print("0 angle_champs "+angle_champs+" angle_positions "+angle_positions+" ligne_coupee_d_emblee "+ligne_coupee_d_emblee+" ligne_coupee "+ligne_coupee+" chp_dpt.pnt ");    
		    cyl[1000]=null;
		}
	    }
	    while(Math.abs(inc)>(float)0.01&&Math.abs(diff_angles)>(float)0.01){
		inc/=2;
		if(prprint)
		    System.out.println("inc "+(float)inc+" diff_signes "+diff_signes);
		int n_c=0;
		boolean repart_en_sens_inverse=false;
		int n_retour_en_arriere=0;
		diff_signes=incremente_et_compare(inc,chp_initial);
		se_rapproche_de_zero=Math.abs(diff_angles)<Math.abs(diff_angles_prec);
		if(prprint){
		    System.out.println(str_increm+" diff_angles "+(float)diff_angles+" diff_angles_prec "+(float)diff_angles_prec+" diff_signes "+diff_signes+" inc "+(float)inc);
		    System.out.println(str_increm+" angle_champs "+(float)angle_champs+" angle_positions "+(float)angle_positions+" somme_inc "+(float)somme_inc);
		}
		boolean dfsgn=diff_signes;
		if(!dfsgn){
		    if(!se_rapproche_de_zero){
			if(repart_en_sens_inverse&&n_retour_en_arriere>=2){
			    if(ligne_coupee)
				cyl[3000]=null;
			    ligne_coupee=true;
			    System.out.println(str_increm+" ligne_coupee");
			    return wowo;
			}
			inc=-inc;
			diff_signes=incremente_et_compare(inc,chp_initial);
			inc/=(float)2.;
			diff_signes=incremente_et_compare(inc,chp_initial);
			if(prprint){
			    System.out.println(str_increm+"££££££ sens inverse indu "+" diff_angles "+(float)diff_angles+" diff_angles_prec "+(float)diff_angles_prec+" diff_signes "+diff_signes+" inc "+(float)inc);
			    System.out.println(str_increm+" angle_champs "+(float)angle_champs+" angle_positions "+(float)angle_positions+" somme_inc "+(float)somme_inc);
			}
			repart_en_sens_inverse=true;
			n_retour_en_arriere++;  
		    }
		}else{
		    inc=-inc;
		    diff_signes=incremente_et_compare(inc,chp_initial);
		    inc/=-(float)2.; 
		    diff_signes=incremente_et_compare(inc,chp_initial);
		    if(prprint){
			
			System.out.println(str_increm+" sens inverse "+" diff_angles "+(float)diff_angles+" diff_angles_prec "+(float)diff_angles_prec+" diff_signes "+diff_signes+" inc "+(float)inc);
			System.out.println(str_increm+" angle_champs "+(float)angle_champs+" angle_positions "+(float)angle_positions+" somme_inc "+(float)somme_inc);
		    }
		    if(!se_rapproche_de_zero)
			inc=-inc;
		}
		//System.out.println("+ i "+i+" inc "+inc+" diff_angles "+(float)diff_angles);
		n_c++;
		if(n_c>100)
		    cyl[2000]=null;
	    
	    //diff_angles0=diff_angles;
	    if(n_c>100)
		cyl[2000]=null;
	    }
	    if(prprint)
	    	System.out.println("fin  num_secteur "+num_secteur+" ndc_lgn "+ndc_lgn+" diff_angles "+(float)diff_angles);
	    vovo.assigne(chp_dpt.chp,pt_ss);
	}
	return vovo;
    }
    vecteur champ_suivant_par_intersections(vecteur chp_initial,float dist,int num_conduc_depart){
	boolean parallele_ou_mauvais_sens=essai_pt(chp_initial,num_conduc_depart);
	//if(i_ens==0)
	// pt_essai.print("parallele_ou_mauvais_sens"+parallele_ou_mauvais_sens+"vovo ");
	if(parallele_ou_mauvais_sens)
	    return vovo;
	
	wawa.assigne(vovo);
	titi.assigne(pt_essai);
	toto.assigne_soustrait(pt_essai,pt_d_intersec);
	//pt_d_intersec.print(" pt_d_intersec ");
	toto.multiplie_cst(dist_pt_init.longueur()/dist_pt_fin.longueur());
	pt_essai.assigne_additionne(pt_d_intersec,toto);
	bibo=point_final_de_la_ligne(pt_essai,dist);
	if(bibo.pt_fn_ligne){
	    return chp_initial;// n'importe quel champ, la ligne est teminee
	}
	//	parallele_ou_mauvais_sens=essai_pt(chp_initial,num_conduc_depart);
	parallele_ou_mauvais_sens=essai_pt(wawa,num_conduc_depart);
	//pt_essai.print("parallele_ou_mauvais_sens"+parallele_ou_mauvais_sens+"vovo ");
	chp_dpt_prec.pnt.assigne_soustrait(pt_essai,chp_initial.pnt);
	if(parallele_ou_mauvais_sens){
	    vovo.assigne(wawa);
	    chp_dpt_prec.pnt.assigne_soustrait(titi,chp_initial.pnt);
	}
	chp_dpt_prec.chp.assigne(vovo.chp);
	return vovo;
    }
    boolean incremente_et_compare(float inc,vecteur chp_initial){
	somme_inc+=inc;
	chp_dpt.pnt.rotation(inc);
	pt_ss.assigne_additionne(chp_initial.pnt,chp_dpt.pnt);
	diff_angles_prec=diff_angles;
	diff_angles=d_angle_chp_pnt(pt_ss,false,chp_initial);
	if (diff_angles0<(float)0.&&diff_angles>0.||diff_angles0>(float)0.&&diff_angles<(float)0.){
	    str_increm="+";
	    return true;
	}else{
	     str_increm="+";
	    return false;
	}
    }
    float d_angle_chp_pnt(point pt_se,boolean initi,vecteur chp_initial){
	if(!initi){
	    chp_dpt.chp.assigne(champ(pt_se));
	    angle_positions=(float)Math.asin(chp_dpt.pnt.produit_vectoriel(chp_dpt_prec.pnt)/(chp_dpt.pnt.longueur()*chp_dpt_prec.pnt.longueur()));
	}else{
	    angle_positions=0;
	}
	angle_champs=(float)Math.asin(chp_dpt.chp.produit_vectoriel(chp_initial.chp)/(chp_dpt.chp.longueur()*chp_initial.chp.longueur()));
	/*	
	  chp_dpt.pnt.print(" chp_dpt.pnt ");
	  chp_dpt_prec.pnt.print(" chp_dpt_prec.pnt ");
	  chp_dpt.chp.print(" chp_dpt.chp ");
	  chp_dpt_prec.chp.print(" chp_dpt_prec.chp ");
	  System.out.println(" angle_champs "+angle_champs+" angle_positions "+angle_positions);
	*/
	if(a_reculon){
	    angle_positions=-angle_positions;
	    angle_champs=-angle_champs;
	} 
	return (angle_champs-angle_positions);
    }
    boolean essai_pt(vecteur chp_prec,int num_conduc_depart){
	cchhpp.assigne(champ(pt_essai));
	vovo.assigne(cchhpp,pt_essai);
	tata.assigne_soustrait(pt_essai,chp_prec.pnt);
	///*
	//if(!(i_ens==1&&num_conduc_depart==nfiltot-1))
	if(cchhpp.longueur_carre()>(float)0.01*champ_de_ref*champ_de_ref){
	    if(Math.abs(chp_dpt_prec.pnt.x)<(float)1000.&&(float)Math.abs(chp_dpt_prec.pnt.y)<(float)1000.){
		prd_vect=vovo.chp.produit_vectoriel(chp_prec.chp)/chp_prec.chp.longueur_carre();
		//if(Math.abs(prd_vect)<0.0001) prd_vect=0;
		prd_vect1=tata.produit_vectoriel(chp_dpt_prec.pnt)/tata.longueur_carre();
		if(prd_vect<(float)-0.001&&prd_vect1>(float)0.01||prd_vect>(float)0.001&&prd_vect1<(float)-0.01){
		    //toto.assigne_facteur(chp_prec.chp,vovo.chp.longueur()/chp_prec.chp.longueur());
		    //vovo.assigne(toto,pt_essai);
		    tata.assigne_additionne(chp_prec.pnt,chp_dpt_prec.pnt);
		    vovo.assigne(cchhpp,tata);
		    return true;
		}
	    }
	}
	//*/
	parametres_droite_finale.assigne(parametres_droite_perp_au_champ(vovo));
	/*
	  if(i_ens==0){
	  System.out.println(" parametres_droite_finale.a "+parametres_droite_finale.a+ " parametres_droite_initiale.a "+parametres_droite_initiale.a);
	  System.out.println(" parametres_droite_finale.b "+parametres_droite_finale.b+ " parametres_droite_initiale.b "+parametres_droite_initiale.b);
	  }
	*/
	if(Math.abs(parametres_droite_finale.a-parametres_droite_initiale.a)<(float)0.00001){	   
	    return true;
	}
	if(parametres_droite_initiale.a>9999999.){
	    pt_d_intersec.x=chp_prec.pnt.x;  
	    pt_d_intersec.y=parametres_droite_finale.a*pt_d_intersec.x+parametres_droite_finale.b;;  
	}else if (parametres_droite_finale.a>9999999.){
	    pt_d_intersec.x=pt_essai.x;
	    pt_d_intersec.y=parametres_droite_initiale.a*pt_d_intersec.x+parametres_droite_initiale.b;
	}else{
	    pt_d_intersec.x=-(parametres_droite_initiale.b-parametres_droite_finale.b)/(parametres_droite_initiale.a-parametres_droite_finale.a);
	    pt_d_intersec.y=parametres_droite_initiale.a*pt_d_intersec.x+parametres_droite_initiale.b;
	}

	dist_pt_init.assigne_soustrait(pt_d_intersec,chp_prec.pnt);
	dist_pt_fin.assigne_soustrait(pt_d_intersec,pt_essai);
	    return false
;
    }
    public biparam parametres_droite_perp_au_champ(vecteur vec){
	biparam bpm= new biparam((float)0.,(float)0.);
	if(vec.chp.y*vec.chp.y/vec.chp.longueur_carre()>(float)0.00000000001){
	float a=-vec.chp.x/vec.chp.y;
	bpm= new biparam(a,vec.pnt.y-a*vec.pnt.x);
	}else
	    bpm= new biparam((float)10000000.,vec.pnt.y);	    
	return bpm;
    }
    biboolean point_final_de_la_ligne(point p_s,float dist){
	hors_cadre=false;
	biboolean bib=new biboolean(false,false);float ttl=0; 
       	for(int iqq=0;iqq<nfiltot;iqq++){
	    toto.assigne_soustrait(p_s,cyl[iqq].centre);
	    ttl=toto.longueur_carre();
	    //toto.print(" toto ");
	    bib.approche_fin_ligne=cyl[iqq].fil_ext&&ttl>(cyl[iqq].rc-20)*(cyl[iqq].rc-20)||!cyl[iqq].fil_ext&&ttl<(cyl[iqq].rc+20)*(cyl[iqq].rc+20);
	    if(cyl[iqq].fil_ext&&ttl>(cyl[iqq].rc-3)*(cyl[iqq].rc-3)||!cyl[iqq].fil_ext&&ttl<(cyl[iqq].rc+3)*(cyl[iqq].rc+3)){
		//if(cyl[iqq].fil_ext&&toto.longueur_carre()>cyl[iqq].rc*cyl[iqq].rc||!cyl[iqq].fil_ext&&toto.longueur_carre()<cyl[iqq].rc*cyl[iqq].rc){
		float tt=toto.direction()*pi/(float)180.;
		if(tt<0)
		    tt+=2*pi;
		numero_zone_arrivee=subject.dim-1-(int)(tt/((float)2.0*pi/subject.dim));
		iq_arrivee=iqq;
		cyl[iqq].zone_faite_arrivee[numero_zone_arrivee]=true;
		distance_avant_fin=Math.abs(Math.abs(dist)-Math.abs(toto.longueur()-cyl[iqq].rc));
		//p_s.print("toto.longueur() "+(float)toto.longueur()+" dist "+(float)dist+" distance_avant_fin "+(float)distance_avant_fin+" iqq "+iqq+" p_s ");
		bib.pt_fn_ligne=true;
	    }
	}
	if(p_s.x<0||p_s.x>right_ens_cyl-left_ens_cyl||p_s.y<0||p_s.y>bottom_ens_cyl-top_ens_cyl){
	    hors_cadre=true; 
	    bib.pt_fn_ligne=true;
	}
	return bib;	
    }
    public void  calculs(){
	System.out.println("debut calculs ");
	if(!calculating){
	    for(int iq=0;iq<nfiltot;iq++){
		for(int iqq=0;iqq<nfiltot;iqq++){
		    n_valeurs_dV[iq][iqq]=0;
		    q_en_regard[iq][iqq]=0;
		}	
		for (int kk=0;kk<subject.dim;kk++)
		    cyl[iq].zone_faite_arrivee[kk]=false;
	    }
	    for(int i=0;i<nombre_de_pts_a_montrer;i++)
		chp_pts_a_montrer[nombre_de_pts_a_montrer]=null;
	    nombre_de_pts_a_montrer=0;
	    boolean tous_zero=true;
	    for(int iq=0;iq<nfiltot;iq++)
		if(Math.abs(cyl[iq].q)>(float)0.001)
		    tous_zero=false;
	    if(tous_zero||(nfiltot==1)){
		for(int iq=0;iq<nfiltot;iq++){
		    for (int kk=0;kk<subject.dim;kk++){
			cyl[iq].sigma[kk]=0;
		    }		    
		    cyl[iq].chp_clc.remplis();
		}
		if(!tous_zero)
		    echelle=calcule_echelle();
		calculs_faits=true;
		if(!en_train_de_peindre)
		    paint();
		System.out.println(" i_ens "+i_ens+" tous_zero "+tous_zero+"retour");
		return;
	    }
	    System.out.println("Attendez une ou deux minutes, je calcule!");
	    
	    for(int iq=0;iq<nfiltot;iq++){
		for (int k=0;k<subject.dim;k++)
		    cyl[iq].sigma[k]=0;
		System.out.println(" Fil numero "+ iq);
	    }
	    for (int iter1=0;iter1<4;iter1++){
		iter=iter1;
		System.out.println("Iteration numero "+ iter);
		for(int iq=0;iq<nfiltot;iq++){
		    System.out.println(" Fil numero "+ iq);
		    boolean recherche_angle=true;
		    cyl[iq].angle=0;
		    for(int l=0;l<2;l++){
			if(!recherche_angle)
			    for (int k=0;k<subject.dim;k++){
				cyl[iq].cos_angle[k]=(float)Math.cos(k*(float)2.0*subject.pi/subject.dim+cyl[iq].angle);
				cyl[iq].sin_angle[k]=(float)Math.sin(k*(float)2.0*subject.pi/subject.dim+cyl[iq].angle);
			    }
			boucle_k:
			for (int k=0;k<subject.dim;k++){
			    champ[k]=champ_pendant_calculs( iq, k ,recherche_angle);
			    if(recherche_angle){
				int km1;
				if(k!=0)
				    km1=k-1;
				else{
				    km1=subject.dim-1;
				    champ[subject.dim-1]=champ_pendant_calculs( iq,subject.dim-1 ,recherche_angle);
				}
				if((champ[k].x>=0)&&(champ[km1].x<(float)0.)){
				    //if(iter==0)cyl[iq].residu=Math.abs(champ[km1].x)/Math.abs(champ[km1].x-champ[k].x);
				    cyl[iq].residu=Math.abs(champ[km1].x)/Math.abs(champ[km1].x-champ[k].x);
				    cyl[iq].angle=(km1+cyl[iq].residu)*(float)2.0*subject.pi/subject.dim;//%%%%
				    cyl[iq].k_e_nul=km1;
				    //if(iter==0)cyl[iq].angle=(km1+Math.abs(champ[km1].x)/Math.abs(champ[km1].x-champ[k].x))*2.0*subject.pi/subject.dim;
				    champ[km1].print("iq "+iq+" cyl[iq].angle "+cyl[iq].angle+" k "+k+" cyl[iq].residu "+cyl[iq].residu+ " champ ");
				    recherche_angle=false;
				    //if(iter==0)break boucle_k;//%%%%
				    break boucle_k;
				}
			    }
			    //if((k==subject.dim-1)&&(l==0)){
			    //if((champ[0]>=0)&&(champ[subject.dim-1]<0.))
			    //recherche_angle=false;cyl[iq].angle=0;
			    //cos_angle[k]=subject.cosinus[k];sin_angle[k]=subject.sinus[k];
			    //System.out.println(" ***cyl[iq].angle "+cyl[iq].angle+" k "+k);
			    //}
			    if(k>0)
				cyl[iq].secmem[k]=-champ[k].x;
			    else
				cyl[iq].secmem[k]=0;
			}
		    }
		}
		for(int iq=0;iq<nfiltot;iq++){
		    champ_centre_cylindre(iq);
		    cyl[iq].sigma_suppl[0]=0;
		    for (int k=1;k<subject.dim;k++){
			cyl[iq].sigma_suppl[k]=0;
			for (int j=1;j<subject.dim;j++)
			    cyl[iq].sigma_suppl[k]+=subject.mat_e_tang.inverse[k][j]*cyl[iq].secmem[j];
			//if(iter>1)cyl[iq].sigma_suppl[k]/=(float)1.5;//%%%%
			cyl[iq].sigma_suppl[0]-=cyl[iq].sigma_suppl[k];
		    }
		    //for (int k=0;k<subject.dim;k++)
		    //  if(cyl[iq].fil_ext)
		    //cyl[iq].sigma_suppl[k]*=-(float)1.;
		}
		for(int iq=0;iq<nfiltot;iq++){
		    //System.out.println(" iq "+iq+" k_e_nul "+cyl[iq].k_e_nul+" residu "+cyl[iq].residu);
		    rearrange(cyl[iq].k_e_nul,cyl[iq].residu,iq);
		    float somme=0;float somme_suppl=0;
		    for (int k=0;k<subject.dim;k++){
			//System.out.println(" k "+k+" iq "+iq+" sigma[k] "+cyl[iq].sigma[k]+" sigma_suppl[k] "+cyl[iq].sigma_suppl[k]);			
			cyl[iq].sigma[k]+=cyl[iq].sigma_suppl[k];
			somme+=cyl[iq].sigma[k]*((float)2.*subject.pi*cyl[iq].rc)/subject.dim;
			somme_suppl+=cyl[iq].sigma_suppl[k]*((float)2.*subject.pi*cyl[iq].rc)/subject.dim;
		    }
		    //System.out.println(" iq "+iq+" somme "+somme+" somme_suppl "+somme_suppl);
		    champ_centre_cylindre(iq);
		}
		for(int iq=0;iq<nfiltot;iq++){
		    for (int i=0;i<8;i++){
			cyl[iq].sigma_cos[i]=0;
			cyl[iq].sigma_sin[i]=0;
			float somme_cos2=0;float somme_sin2=0;
			for (int k=0;k<subject.dim;k++){
			    cyl[iq].sigma_cos[i]+=cyl[iq].sigma[k]*subject.cocos[k][i];
			    cyl[iq].sigma_sin[i]+=cyl[iq].sigma[k]*subject.sisin[k][i];
			}
			cyl[iq].sigma_cos[i]/=(subject.dim/(float)2.);
			cyl[iq].sigma_sin[i]/=(subject.dim/(float)2.);
		    }
		    for (int k=0;k<subject.dim;k++){
			float sisig=0;
			for (int i=0;i<8;i++)
			    sisig+=cyl[iq].sigma_cos[i]*subject.cocos[k][i]+cyl[iq].sigma_sin[i]*subject.sisin[k][i];
			//System.out.println(" k "+k+" cyl[iq].sigma "+cyl[iq].sigma[k]+" sisig "+sisig);
			cyl[iq].sigma[k]=sisig;
		    }
		}
	    }
	    echelle=calcule_echelle();
	    calculs_faits=true;
	    System.out.println(" debut lignes de champ ");

	    calculating=false;
	}	
    }
    void lignes_de_champ(){
	comment="Attendez un petit moment svp ";
	for(int iq=0;iq<nfiltot;iq++){
	    //int iq=0;{
	    vecteur vvv=new vecteur(zer,zer);
	    int nb_de_pts_trouves=0;
	    kk_plus_precis=0;
	    for (int kk=0;kk<subject.dim;kk++){
		//for (int kk=1;kk<=2;kk++){
		//int kk=0;{
		num_secteur=kk;
		a_reculon=cyl[iq].chp_clc.charge_secteur[kk]<0;
		int kkk=kk+1;
		if(kkk==subject.dim)kkk=0;
		int lll=kk-1;
		if(lll==-1)lll=subject.dim-1;
		champ_de_ref=cyl[iq].chp_clc.champ_moyen;
		draw_point=(iq==fil_initial_ligne_a_montrer||i_ens==0&&(!subject.ensemble_identiques||demo)&&iq!=fil_initial_ligne_a_montrer&&!cyl[iq].zone_faite_arrivee[kk]);
		ligne_coupee=false;ligne_coupee_d_emblee=false;
		vvv.assigne(trouve_ligne_de_champ(iq,kk,false,(float)0.,1));
		if(ligne_coupee||ligne_coupee_d_emblee){
		    System.out.println("0. on va vers l'essai 2");
		    vvv.assigne(trouve_ligne_de_champ(iq,kk,false,(float)0.,2));
		    if(ligne_coupee||ligne_coupee_d_emblee){
			System.out.println("iq "+iq+" kk "+kk);
			cyl[1350]=null;
		    }
		}
		//System.out.println("iq "+iq+" kk "+kk);
		if(!hors_cadre){
		    if(diff_potentiel<0)
			distance_avant_fin=-distance_avant_fin;
		    diff_potentiel+=distance_avant_fin*vvv.chp.longueur()*(float)1.e-9;
		    // le 1.e-9 vient des charges en ncb/m
		    dV[iq][iq_arrivee][kk]=diff_potentiel;
		    numeros_kk[iq][iq_arrivee][n_valeurs_dV[iq][iq_arrivee]]=kk;
		    n_valeurs_dV[iq][iq_arrivee]++;
		    if(!(i_ens==0&&(!subject.ensemble_identiques||demo))){
			toto.assigne(subject.cosinus[kk],-subject.sinus[kk]);
			float ddqq=cyl[iq].chp_clc.vecteur_champ[kk].chp.scalaire(toto)*dtet*4*cyl[iq].rc*subject.eps0;
			if(cyl[iq].fil_ext)
			    ddqq=-ddqq;
			if(iq_arrivee==iq_arrivee_prec||kk==0)
			    q_en_regard[iq][iq_arrivee]+=ddqq;
			//System.out.println("kk "+kk+" iq "+iq+" iq_arrivee "+iq_arrivee+" dV[iq][iq_arrivee][kk] "+(float)dV[iq][iq_arrivee][kk]+"dqq "+(float)ddqq );
			int iqq=iq_arrivee;
			if(!(i_ens==0&&(!subject.ensemble_identiques||demo)))//&&iq_arrivee==1)
			    System.out.println(" iq "+iq+" iq_arrivee "+iq_arrivee+" kk "+kk+" q_en_regard[iq][iq_arrivee] "+(float)q_en_regard[iq][iq_arrivee]);
			if(!demo&&iq_arrivee!=iq_arrivee_prec&&nb_de_pts_trouves!=0&&iq!=iq_arrivee){
			    boolean drwpt=draw_point;
			    draw_point=false;
			    kk_plus_precis=0;int iq_nouveau=iq_arrivee;
			    difference_pt.assigne_soustrait(cyl[iq].chp_clc.vecteur_champ[kk].pnt,cyl[iq].chp_clc.vecteur_champ[lll].pnt);
			    difference_chp.assigne_soustrait(cyl[iq].chp_clc.vecteur_champ[kk].chp,cyl[iq].chp_clc.vecteur_champ[lll].chp);
			    System.out.println(" plus-precis "+" iq "+iq+" iq_arrivee "+iq_arrivee+" iq_arrivee_prec "+iq_arrivee_prec+" kk "+kk);
			    //while (iq_arrivee!=iq_arrivee_prec&&iq_arrivee!=iqq){
			    //while (iq_arrivee!=iq_arrivee_prec&&kk_plus_precis>-1.&&kk_plus_precis<0.01){
			    while (iq_arrivee!=iq_arrivee_prec){
				ligne_coupee=false;ligne_coupee_d_emblee=false;
				vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)-0.5,1));
				if(ligne_coupee||ligne_coupee_d_emblee){
				    kk_plus_precis-=((float)-0.5);
				    System.out.println("-0.5 on va vers l'essai 2");
				    vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)-0.5,2));
				}
				//System.out.println(" -0.5 "+" kk_plus_precis "+kk_plus_precis);
				//if(kk_plus_precis<-1.){
				//System.out.println(" kk_plus_precis "+kk_plus_precis+" iq_arrivee "+iq_arrivee+" iq_arrivee_prec "+iq_arrivee_prec+ "iqq "+iqq);
				if(kk_plus_precis<(float)-10.)
				    cyl[1000]=null;
				//}
			    }
			    //while (iq_arrivee==iq_arrivee_prec&&kk_plus_precis>-1.&&kk_plus_precis<(float)0.01){
			    while (iq_arrivee==iq_arrivee_prec){
				ligne_coupee=false;ligne_coupee_d_emblee=false;
				vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)0.25,1));
				if(ligne_coupee||ligne_coupee_d_emblee){
				    kk_plus_precis-=((float)0.25);
				    System.out.println("0.25 on va vers l'essai 2");
				    vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)0.25,2));
				}
				//System.out.println(" 0.25 "+" kk_plus_precis"+kk_plus_precis);
			    }
			    //while (iq_arrivee!=iq_arrivee_prec&&iq_arrivee!=iqq){
			    //while (iq_arrivee!=iq_arrivee_prec&&kk_plus_precis>-1.&&kk_plus_precis<0.01){
			    while (iq_arrivee!=iq_arrivee_prec){
				ligne_coupee=false;ligne_coupee_d_emblee=false;
				vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)-0.125,1));
				if(ligne_coupee||ligne_coupee_d_emblee){
				    kk_plus_precis-=(float)(-0.125);
				    System.out.println("-0.125 on va vers l'essai 2");
				    vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)-0.125,2));
				}
				//System.out.println(" -0.125 "+" kk_plus_precis"+kk_plus_precis);
			    }
			    //while (iq_arrivee==iq_arrivee_prec&&kk_plus_precis>-1.&&kk_plus_precis<0.01){
			    while (iq_arrivee==iq_arrivee_prec){
				ligne_coupee=false;ligne_coupee_d_emblee=false;
				vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)0.0625,1));
				if(ligne_coupee||ligne_coupee_d_emblee){
				    kk_plus_precis-=((float)0.0625);
				    System.out.println("0.0625 on va vers l'essai 2");	
				    vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)0.0625,2));
				}
				// 0.0625 "+" kk_plus_precis"+kk_plus_precis);
			    }
			    //while (iq_arrivee!=iq_arrivee_prec&&iq_arrivee!=iqq)
			    //while (iq_arrivee!=iq_arrivee_prec&&kk_plus_precis>-1.&&kk_plus_precis<0.01)
			    while (iq_arrivee!=iq_arrivee_prec){
				ligne_coupee=false;ligne_coupee_d_emblee=false;
				vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)-0.03125,1));
				if(ligne_coupee||ligne_coupee_d_emblee){
				    kk_plus_precis-=((float)-0.03125);
				    System.out.println("0.03125 on va vers l'essai 2");	
				    vvv.assigne(trouve_ligne_de_champ(iq,kk,true,(float)-0.03125,2));
				}
			    }
			    q_en_regard[iq][iq_arrivee]+=((float)1.+kk_plus_precis)*ddqq;
			    q_en_regard[iq][iq_nouveau]-=kk_plus_precis*ddqq;
			    if(!(i_ens==0&&(!subject.ensemble_identiques||demo)))//&&iq_arrivee==1)
				System.out.println(" -0.03125 "+" kk_plus_precis"+kk_plus_precis+" ddqq "+(float)ddqq+" numero_zone_arrivee "+numero_zone_arrivee+" q_en_regard[iq][iq_arrivee] "+(float)q_en_regard[iq][iq_arrivee] +" q_en_regard[iq][iq_nouveau]"+(float)q_en_regard[iq][iq_nouveau]);
			    draw_point=drwpt;
			}
			iq_arrivee_prec=iqq;
			nb_de_pts_trouves++;
			//nombre_de_pts_a_montrer--;
			//System.out.println(" distance_avant_fin "+distance_avant_fin+" dist "+dist);
		    }
		}
		//System.out.println("i "+indice_ligne+" iq "+iq+" kk "+kk+" diff_potentiel "+(float)diff_potentiel);
		//break;
		//}else
		//break;
	    }
	    //if(indice_ligne>0)
	    //cyl[iq].zone_faite_arrivee[kk]=true;
	    if(!hors_cadre)
		System.out.println("ùùùùùùùùù i_ens "+i_ens+"iq_arrivee "+iq_arrivee+" numero_zone_arrivee "+numero_zone_arrivee);
	}
	/*
	  for (int i=0;i<nombre_de_pts_a_montrer;i++){
	  cyl[iq].chp_clc.vecteur_champ[i].print("i "+i+" cyl[iq].chp_clc.vecteur_champ[i]");
	  chp_pts_a_montrer[i].print( "chp_pts_a_montrer[i]");
	  }
	*/
	for(int iq=0;iq<nfiltot;iq++){
	    //for(int kk=0;kk<subject.dim;kk++)
	    //cyl[iq].chp_clc.vecteur_champ[kk].chp.print(" kk "+kk+" vecteur_champ[kk].chp ");
	    cyl[iq].chp_clc.somme_champs.print("iq "+iq+" somme_charges "+cyl[iq].chp_clc.somme_charges+" somme_charges_pos "+cyl[iq].chp_clc.somme_charges_pos+" somme_charges_neg "+cyl[iq].chp_clc.somme_charges_neg+" somme_champs ");
	}
	int jj=-1;
	for(int iq=0;iq<nfiltot;iq++){
	    for(int iqq=0;iqq<nfiltot;iqq++){
		if(iq!=iqq){
		    dV_moyen[iq][iqq]=0;
		    dV2_moyen[iq][iqq]=0;
		    for(int i=0;i<n_valeurs_dV[iq][iqq];i++){
			jj=numeros_kk[iq][iqq][i];
			dV_moyen[iq][iqq]+=dV[iq][iqq][jj];
			dV2_moyen[iq][iqq]+=(dV[iq][iqq][jj]*dV[iq][iqq][jj]);
		    }
		    if(n_valeurs_dV[iq][iqq]>0){
			dV_moyen[iq][iqq]/=(n_valeurs_dV[iq][iqq]);
			dV2_moyen[iq][iqq]/=(n_valeurs_dV[iq][iqq]);
			dV2_moyen[iq][iqq]=(float)Math.sqrt(dV2_moyen[iq][iqq]-dV_moyen[iq][iqq]*dV_moyen[iq][iqq]);
		    }
		    //System.out.println("jj "+jj+" iq "+iq+" iqq "+iqq+" q_en_regard[iq][iqq] "+q_en_regard[iq][iqq]);
		}					      
	    }
	}
	lignes_de_champ_faites=true;
	attend_souris=false;
	//data.ecrit_donnees();
    }
    vecteur trouve_ligne_de_champ(int iq,int kk,boolean plus_precis,float rapport,int essai){
	//System.out.println(" nombre_de_pts_a_montrer "+nombre_de_pts_a_montrer);
       	//recommencer_en_cas_d_echec: //dans ce cas,ligne_coupee==true
	gr_ensemble.setColor(Color.black);
	if(demo)
	    gr_ensemble.setFont(subject.times_gras_14);
	else
	    gr_ensemble.setFont(subject.times_gras_24);
	gr_ensemble.drawString("Attendez un peu, svp",100,200);
	gr_ensemble.setFont(subject.times_gras_14);
        bibo.assigne(false,false);
	distance_avant_fin=0;
	nombre_de_pts_a_montrer_init=nombre_de_pts_a_montrer;
	int indice_ligne=0;diff_potentiel=0;float dist=distance_quadrillage;
	chp_dpt_prec.pnt.assigne((float)1000.,(float)1000.);
	while (!bibo.pt_fn_ligne){
	    if(indice_ligne>100||nombre_de_pts_a_montrer>9990)
	    	break;
	    if(indice_ligne==0){
		if(!plus_precis)
		    wowo.assigne(cyl[iq].chp_clc.vecteur_champ[kk]);
		else{
		    kk_plus_precis+=rapport;
		    //System.out.println(" kk_plus_precis "+kk_plus_precis);
		    titi.assigne_facteur(difference_pt,kk_plus_precis);
		    titi.assigne_additionne(cyl[iq].chp_clc.vecteur_champ[kk].pnt,titi);
		    toto.assigne_facteur(difference_chp,kk_plus_precis);
		    toto.assigne_additionne(cyl[iq].chp_clc.vecteur_champ[kk].chp,toto);
		    wowo.assigne(toto,titi);
		}
		toto.assigne_soustrait(wowo.pnt,cyl[iq].centre);
		if(wowo.chp.scalaire(toto)<0&&!cyl[iq].fil_ext||wowo.chp.scalaire(toto)>0&&cyl[iq].fil_ext)
		    dist=-distance_quadrillage;
	    }else
		wowo.assigne(chp_pts_a_montrer[nombre_de_pts_a_montrer-1]);
	    
	    chp_pts_a_montrer[nombre_de_pts_a_montrer]=new vecteur(champ_suivant(wowo,dist,iq,indice_ligne,essai));
	    fil_initial_de_la_ligne[nombre_de_pts_a_montrer]=iq;
	    if((ligne_coupee||ligne_coupee_d_emblee)){
		nombre_de_pts_a_montrer=nombre_de_pts_a_montrer_init;
		System.out.println("essai "+essai+"$$$$$ ligne_coupee "+ligne_coupee+" rapport "+(float)rapport);
		return wowo;
	    }
	    champ_du_point_a_dessiner[nombre_de_pts_a_montrer]=draw_point&&!(montrer_un_champ_sur_2&&nombre_de_pts_a_montrer/2*2==nombre_de_pts_a_montrer);
	    /*
	      pt_essai.print("$$$ distance_avant_fin "+distance_avant_fin+" pt_essai " );
	      chp_pts_a_montrer[nombre_de_pts_a_montrer].chp.print(" chp_pts_a_montrer[nombre_de_pts_a_montrer].chp ");
	      chp_pts_a_montrer[nombre_de_pts_a_montrer].pnt.print(" chp_pts_a_montrer[nombre_de_pts_a_montrer].pnt ");
	    */
	    if(!bibo.pt_fn_ligne){
		dist_reelle.assigne_soustrait(chp_pts_a_montrer[nombre_de_pts_a_montrer].pnt,wowo.pnt);
		toto.assigne_additionne(wowo.chp,chp_pts_a_montrer[nombre_de_pts_a_montrer].chp);
		diff_potentiel+=(dist_reelle.scalaire(toto)/2)*(float)1.e-9;
		//System.out.println(" i "+indice_ligne+" iq "+iq+" kk "+kk+" kk_plus_precis "+(float)kk_plus_precis);
		//if(indice_ligne==2)
		//	chp_pts_a_montrer[3000]=null;
		nombre_de_pts_a_montrer++;
		indice_ligne++;
	    }
	}
	/*
	if(i_ens==0&&kk==0&&!demo){
	    pt_essai.print(" distance_avant_fin "+distance_avant_fin+" pt_essai " );
	    cyl[1000]=null;
	}
	*/
	if(!(i_ens==0&&(!subject.ensemble_identiques||demo)))
	    System.out.println(" iq "+iq+" nombre_de_pts_a_montrer_init "+nombre_de_pts_a_montrer_init+" nombre_de_pts_a_montrer "+nombre_de_pts_a_montrer);

	return wowo;
    }
    public float calcule_echelle(){
	float ec=(float)1.;float vec2_max=0;
	for(int iq=0;iq<nfiltot;iq++){
	    cyl[iq].chp_clc.remplis();
	    for(int k=0;k<subject.dim;k++)
		if(cyl[iq].chp_clc.vecteur_champ[k].chp.longueur_carre()>vec2_max)
		    vec2_max=cyl[iq].chp_clc.vecteur_champ[k].chp.longueur_carre();
	}
	float sqq=(float)Math.sqrt(vec2_max);
	ec=(float)20./(float)Math.sqrt(vec2_max);
	System.out.println(" ec"+ec+" sqq "+sqq);
	//System.out.println("Calculs termines echelle"+echelle);
	return ec;
    }
    public void rearrange(int k_e_nul,float residu,int iq){
	float sig[]=new float [64];
	for (int k=0;k<subject.dim;k++){
	    int kk=k+k_e_nul;if(kk>=subject.dim)kk-=subject.dim;
	    int km1=k-1;if(km1<0)km1+=subject.dim;
	    sig[kk]=cyl[iq].sigma_suppl[k]*((float)1.-residu)+cyl[iq].sigma_suppl[km1]*(residu);
	}
	for (int k=0;k<subject.dim;k++)
	    cyl[iq].sigma_suppl[k]=sig[k];
    }
    public void verif(int iq){
	for (int k=0;k<subject.dim;k++){
	    champ[k].x=0;
	    float cc=subject.cosinus[k];
	    float ss=subject.sinus[k];
	    for (int j=0;j<subject.dim;j++){
		if(j!=k){
		    float distx=cyl[iq].rc*(cc-subject.cosinus[j]);
		    float disty=cyl[iq].rc*(ss-subject.sinus[j]);
		    float dist=(float)Math.pow(disty,2)+(float)Math.pow(distx,2);
		    champ[k].x+=cyl[iq].sigma_suppl[j]*cyl[iq].rc*((float)2.0*subject.pi/subject.dim)/(2*subject.pi*dist)*(-distx*ss+disty*cc);	
		}
	    }
	    System.out.println("iq "+iq+" k "+k+" champ[k].x "+champ[k].x+" cyl[iq].secmem[k] "+cyl[iq].secmem[k]);
	}
	for (int jj=0;jj<8;jj++){
	    System.out.println(" jj "+jj+"cyl[iq].sigma_cos[jj] "+cyl[iq].sigma_cos[jj]);
	    System.out.println(" jj "+jj+"cyl[iq].sigma_sin[jj] "+cyl[iq].sigma_sin[jj]);
	}
    }
    public void cree_fils (){
	int nombens;
	if(i_demarre==0){
	    if(i_ens==0&&(!subject.ensemble_identiques||demo)){
		nfiltot=1;
		cyl[0]=new cylindre(300,150,60,20,false);
	    }else{
		nfiltot=2;
		cyl[0]=new cylindre(300,300,280,-20,true);
		cyl[1]=new cylindre(200,300,140,20,false);
	    }
	}
	if(i_demarre==1){
	    if(i_ens==0&&(!subject.ensemble_identiques||demo)){
		nfiltot=2;
		if(demo){
		    cyl[0]=new cylindre(60,150,40,20,false);
		    cyl[1]=new cylindre(200,150,40,-20,false);
		}else{
		    cyl[0]=new cylindre(120,300,80,20,false);
		    cyl[1]=new cylindre(400,300,80,-20,false);
		}
	    }else{
		nfiltot=3;
		if(demo){
		    cyl[0]=new cylindre(75,150,40,20,false);
		    cyl[1]=new cylindre(215,150,40,-20,false);
		    cyl[2]=new cylindre(145,150,130,0,true);
		}else{
		    cyl[0]=new cylindre(150,300,80,20,false);
		    cyl[1]=new cylindre(430,300,80,-20,false);
		    cyl[2]=new cylindre(290,300,260,0,true);
		}
	    }
	}
	if(i_demarre==2){
	    if(i_ens==0&&(!subject.ensemble_identiques||demo)){
		nfiltot=3;
		cyl[0]=new cylindre(160,200,80,20,false);
		cyl[1]=new cylindre(440,200,80,-10,false);
		cyl[2]=new cylindre(300,400,80,-10,false);
	    }else{
		nfiltot=4;
		cyl[0]=new cylindre(160,200,80,20,false);
		cyl[1]=new cylindre(440,200,80,-10,false);
		cyl[2]=new cylindre(300,400,80,-10,false);
		cyl[3]=new cylindre(300,300,280,0,true);
	    }
	}
	if(i_demarre==3){
	    if(i_ens==0&&(!subject.ensemble_identiques||demo)){
		nfiltot=4;
		cyl[0]=new cylindre(160,200,80,20,false);
		cyl[1]=new cylindre(440,200,80,-20,false);
		cyl[2]=new cylindre(160,400,80,-20,false);
		cyl[3]=new cylindre(440,400,80,20,false);
	    }else{
		nfiltot=5;
		cyl[0]=new cylindre(160,200,80,20,false);
		cyl[1]=new cylindre(440,200,80,-20,false);
		cyl[2]=new cylindre(160,400,80,-20,false);
		cyl[3]=new cylindre(440,400,80,20,false);
		cyl[4]=new cylindre(300,300,280,0,true);
	    }
	}
    }
    
    public void	traite_click(){
	//System.out.println("entree traite_click ");
	if(cliquee||draguee||relachee||pressee){
	    Date maintenant=new Date();
	    subject.temps_initial_en_secondes=subject.temps_en_secondes(maintenant);
	    if(command!=""){
		boolean succes_menus=gere_menus_souris ();
		du_nouveau=true;
	    }
	}
	if(cliquee){
	    if(!((ppmouseh==ppmouseh_prec)&&(ppmousev==ppmousev_prec))){
		ppmouseh_prec=ppmouseh;ppmousev_prec=ppmousev;	
	    }else
		cliquee=false;
	}

	if(draguee||relachee||pressee||cliquee)
	   du_nouveau=true;
	//System.out.println("fin_gere_menus_souris "+fin_gere_menus_souris);
	//System.out.println(" dans le traite click  3");
	}
    class MouseMotion extends MouseMotionAdapter
    {
	ensemble_de_cylindres subj;
	public MouseMotion (ensemble_de_cylindres a)
	{
	    subj=a;
	}
	public void mouseMoved(MouseEvent e)
	{ppmouseh=e.getX();ppmousev=e.getY();draguee=false;}
	public void mouseDragged(MouseEvent e)
	{ppmouseh=e.getX();ppmousev=e.getY();draguee=true;
	//System.out.println("draguee dans Mousemove "+draguee);
	traite_click();
	}
    }
    
    class MouseStatic extends MouseAdapter
    {
	ensemble_de_cylindres subj;
	public MouseStatic (ensemble_de_cylindres a)
	{
	    subj=a;
	}
	public void mouseClicked(MouseEvent e)
	{
	    ppmouseh=e.getX();ppmousev=e.getY();
	    cliquee=true;
	    System.out.println("cliquee "+cliquee);
	    traite_click();
	    //	System.out.println("ens_de_cyl[icylindre].nb_el_ens "+ens_de_cyl[icylindre].nb_el_ens);
	    //System.out.println("icylindre "+icylindre);
	    //for( int iq=0;iq<ens_de_cyl[icylindre].nb_el_ens;iq++)
	}
	public void mousePressed(MouseEvent e){
	    ppmouseh=e.getX();ppmousev=e.getY();pressee=true;relachee=false;
	    System.out.println("pressee "+pressee);
	    traite_click();
	}
	public void mouseReleased(MouseEvent e){
	    ppmouseh=e.getX();ppmousev=e.getY();
	    cliquee=true;relachee=true;pressee=false;
	    System.out.println("relachee "+relachee);
	    traite_click();
	}
    }
    class cylindre {
	float xc,yc,rc,q,angle,residu;point centre;
	int k_e_nul;
	float cos_angle[]=new float[64];float sin_angle[]=new float[64];
	float sigma_cos[]=new float[8];
	float sigma_sin[]=new float[8];
	float sigma[]=new float[64];
	point point_surface[]=new point[256];
	boolean interieur,fil_ext;
	float secmem[]=new float [64];
	float sigma_suppl[]=new float [64];
	champs_points_calcules chp_clc;
	boolean zone_faite_arrivee[]=new boolean [64];
	public cylindre(int x,int y,int r,int q1, boolean fil_ext1){
	    xc=(float)x;yc=(float)y;rc=(float)r;q=(float)q1; 
	    centre=new point(xc,yc);interieur=false;
	    angle=0;residu=0;
	    fil_ext=fil_ext1;
	    float th=-dtet/(float)2.;
	    for (int kk=0;kk<4*subject.dim;kk++){
		th+=dtet;
		point_surface[kk]=new point(rc*(float)Math.cos(th),rc*(float)Math.sin(th));
	    }
	    for (int k=0;k<subject.dim;k++){
		sigma_suppl[k]=0;sigma[k]=0;cos_angle[k]=0;sin_angle[k]=0;
		zone_faite_arrivee[k]=false;
	    }
	    chp_clc= new champs_points_calcules();
	}
	class champs_points_calcules{
	    vecteur vecteur_champ[]=new vecteur[64];
	    float champ_moyen=0;
	    float charge_secteur[]=new float[64];
	    float facteur_max;point somme_champs;float somme_charges=0,somme_charges_pos=0,somme_charges_neg=0;
	    public champs_points_calcules(){
		for (int kk=0;kk<subject.dim;kk++)
		    vecteur_champ[kk]=new vecteur(zer,zer);
	        somme_champs=new point(zer);
	    }
	    public void remplis(){
		facteur_max=0; somme_champs.assigne(zer);
		somme_charges=0;somme_charges_pos=0;somme_charges_neg=0;
		for (int kk=0;kk<subject.dim;kk++){
		    point pt=new point(xc+rc*subject.cosinus[kk],yc-rc*subject.sinus[kk]);
		    point vec_un=new point(subject.cosinus[kk],-subject.sinus[kk]);
		    float facteur=0;
		    if(kk!=0)
			facteur=sigma[subject.dim-kk]/subject.eps0;
		    else
			facteur=sigma[0]/subject.eps0;
		    //float facteur=sigma[kk]/subject.eps0;
		    if(Math.abs(facteur)>facteur_max)facteur_max=facteur;
		    if(Math.abs(facteur)>facteur_max)facteur_max=facteur;
		    if(fil_ext)
			facteur*=-(float)1.;
		    vec_un.multiplie_cst(facteur);
		    point vec_un_p=new point(subject.cosinus[kk],-subject.sinus[kk]);
			float fact_q;
		    if(!fil_ext)
			fact_q=q/((float)2.*subject.pi*rc*subject.eps0);
		    else
			fact_q=-q/((float)2.*subject.pi*rc*subject.eps0);
		    vec_un.additionne_facteur(vec_un_p,fact_q);
		    //if((i_ens==1)&&((kk==0)||(kk==8)||(kk==16)||(kk==24)))
		    //vec_un.print("kk  "+kk+" facteur "+facteur+" fact_q "+fact_q+" sigma[kk] "+sigma[kk]+" vec_un ");
		    vecteur_champ[kk].assigne(vec_un,pt);
		    somme_champs.additionne(vec_un);
		    charge_secteur[kk]=vec_un.scalaire(vec_un_p)*dtet*4*rc*subject.eps0;
		    somme_charges+=charge_secteur[kk];
		    if(charge_secteur[kk]>0)
			somme_charges_pos+=charge_secteur[kk];
		    else
			somme_charges_neg+=charge_secteur[kk];
		    champ_moyen+=vec_un.longueur();
		}
		champ_moyen/=subject.dim;
	    }
	    public void va_dessiner(){
		for (int kk=0;kk<subject.dim;kk++)
		    vecteur_champ[kk].dessine(echelle,fct_zm_sspl,gr_ensemble,Color.blue);
	    }
	}
    }
    class donnees extends Frame {
	int top_donnees=540, left_donnees=left_ens_cyl+i_ens*600, bottom_donnees= 710, right_donnees=right_ens_cyl+i_ens*600;Graphics grp_donnees;
	String cadre[]=new String[6];int i_demarre;int nb_lignes=10; 
	public donnees(String s){
	    super(s);
	    System.out.println("cree graphe "+s+" i_demarre "+i_demarre );
	    if(demo){
		top_donnees=bottom_ens_cyl/2+150;
		left_donnees=left_ens_cyl+i_ens*600/2;
		right_donnees=right_ens_cyl+i_ens*600/2;
		bottom_donnees=top_donnees+150;
		if(demo){
		    top_donnees-=20;
		    bottom_donnees-=20;
		}
	    }    
	    setSize(right_donnees-left_donnees,bottom_donnees-top_donnees);
	    setLocation(left_donnees,top_donnees);
	    //setLocation(left,top);
	    setVisible(true);
	    if(demo)
		left_donnees=left_ens_cyl+i_ens*600/2;
	    grp_donnees= getGraphics(); 
	    grp_donnees.setColor(Color.black);
	    cadre[1]="";
	    cadre[2]="Conducteur A                               B";
	    cadre[3]="Conducteur A                               B                               C";
	    cadre[4]="Conducteur A                               B                               C                               D";
	    cadre[5]="Conducteur A                               B                               C                               D                               E";
	}
	public void ecrit_donnees(){
	    setVisible(true);
	    //subject.eraserect(grp_donnees,top_donnees,left_donnees,bottom_donnees,right_donnees,Color.white);
	    subject.eraserect(grp_donnees,0,0,bottom_donnees-top_donnees,right_donnees-left_donnees,Color.white);
	    grp_donnees.setColor(Color.black);
	    grp_donnees.drawString(cadre[nfiltot],20,50);
	    for(int iq=0;iq<nfiltot;iq++){
		String st=nom_conduc[iq];
		for(int iqq=0;iqq<nfiltot;iqq++){
		    if(iq!=iqq){
			st+="   "+(int)Math.round(dV_moyen[iq][iqq])+"V ";  
			if(!(i_ens==0&&(!subject.ensemble_identiques||demo)))
			    st+=((float)Math.round(q_en_regard[iq][iqq]*(float)10.)/(float)10.+"nCb/m ");
			else
			    //st+=("V+-"+(int)Math.round(dV2_moyen[iq][iqq])+"V");
			    st+=("+-"+(int)Math.round(dV2_moyen[iq][iqq])+"V");
			if(Math.abs(dV_moyen[iq][iqq])<0.02)
			    st+="  ";
		    }else{
			st+="                                      ";
			//if(i_ens==1)
			//st+="                                        ";
		    }
		}
		grp_donnees.drawString(st,20,66+iq*16);	    
	    }
	}
    }
    class biparam{
	float a,b;    
	biparam(float ai, float bi){
	    a=ai;b=bi;
	}
	void assigne(biparam bp){
	    a=bp.a;b=bp.b;
	}
    }
    class biboolean{
	boolean pt_fn_ligne,approche_fin_ligne;    
	biboolean(boolean ai,boolean bi){
	    pt_fn_ligne=ai;approche_fin_ligne=bi;
	}
	void assigne(biboolean bp){
	    pt_fn_ligne=bp.pt_fn_ligne;approche_fin_ligne=bp.approche_fin_ligne;
	}
	void assigne(boolean ai,boolean bi){
	    pt_fn_ligne=ai;approche_fin_ligne=bi;
	}
    }
}
class vecteur{
    static final float pi=(float)3.141592652;
    point chp;point pnt;
    public vecteur (vecteur v){
	chp=new point(v.chp);
	pnt=new point(v.pnt);
    }
    public vecteur (point v, point p){
	chp=new point(v);
	pnt=new point(p);
    }
    public void assigne ( vecteur v){
	chp.assigne(v.chp);
	pnt.assigne(v.pnt);
    }
    public void assigne(point v, point p){
	chp.assigne(v);
	pnt.assigne(p);
    }
    public void assigne_soustrait ( vecteur v,vecteur www){
	chp.assigne_soustrait(v.chp,www.chp);
	pnt.assigne_soustrait(v.pnt,www.pnt);
    }
    public void print(String st){
	System.out.println(st+ " chp.x "+(float)chp.x+" chp.y "+(float)chp.y+" pnt.x "+(float)pnt.x+" pnt.y "+(float)pnt.y);
    }

    public void dessine( float fzoom,float fct_zm_sspl,Graphics g,Color col){
	
	int x_ini=(int)Math.round(pnt.x); int y_ini=(int)Math.round(pnt.y);
	int x_fin=x_ini+(int)(chp.x*fzoom*fct_zm_sspl);int y_fin=y_ini+(int)(chp.y*fzoom*fct_zm_sspl);
	g.setColor(col);
	//System.out.println(" x_ini "+x_ini+" y_ini "+y_ini+" x_fin "+x_fin+" y_fin "+y_fin);	    
	g.drawLine(x_ini, y_ini, x_fin, y_fin);
	float direct=chp.direction()*pi/(float)180.;
	float dir=direct+3*pi/(float)4.;
	int xf1=x_fin+(int)((float)7.0*(float)Math.cos(dir));int yf1=y_fin+(int)((float)7.0*(float)Math.sin(dir));
	g.drawLine(x_fin, y_fin, xf1, yf1);
	dir=direct-3*pi/(float)4.;
	xf1=x_fin+(int)((float)7.0*(float)Math.cos(dir));yf1=y_fin+(int)((float)7.0*(float)Math.sin(dir));
	g.drawLine(x_fin, y_fin, xf1, yf1);
    }
}
class point implements Cloneable{
    static final float pi=(float)3.141592652;
    float x,y;    
    point(float xi, float yi){
	x=xi;y=yi;
    }
    point(point a){
	x=a.x;y=a.y;
    }
    point(point a,float b){
	x=a.x*b;y=a.y*b;
    }
    public Object clone(){
	try{
	    //point e=(point)super.clone();
	    //return e;
	    return super.clone();
	}
	catch (CloneNotSupportedException e){
	    return null;
	}
	
    }
    public void zero(){
	x=0;y=0;
    }
    public float direction(){
	float angle=0;
	if((float)Math.abs(x)>(float)Math.abs(y)){
	    angle=(float)180./pi*(float)Math.asin(y/longueur());
	    if(x<0.)
		if(y>0)
		    angle=(float)180.-angle;
		else
		    angle=-(float)180.-angle;
	}else{
	    angle=(float)180./pi*(float)Math.acos(x/longueur());
	    if(y<0.)angle=-angle;
	}
	return angle;
    }
    public void projections(float cosinus,float sinus)
    {
        float x_p=x;float y_p=y;
        x=-sinus*x_p+cosinus*y_p;
        y=cosinus*x_p+sinus*y_p;
    }
     public void assigne(float xi, float yi){
	x=xi;y=yi;
    }
    public void assigne(point a){
	x=a.x;y=a.y;
    }
    public void assigne_oppose(point a){
	x=-a.x;y=-a.y;
    }
    public void assigne_additionne(point a,point b){
	x=a.x+b.x;y=a.y+b.y;
    }
    public void assigne_soustrait(point a,point b){
	x=a.x-b.x;y=a.y-b.y;
    }
    public void assigne_facteur(point a,float b){
	x=a.x*b;y=a.y*b;
    }
    public void assigne_diviseur(point a,float b){
	x=a.x/b;y=a.y/b;
    }
    public float distance_carre(point pt){
	float d;
	d=(float)Math.pow(x-pt.x,2)+(float)Math.pow(y-pt.y,2);
	return d;
    }
    public void multiplie_cst(float a){
	x*=a;
	y*=a;
    }
    public void additionne(float xx,float yy){
	x+=xx;
	y+=yy;
    }
    public void additionne(point a){
	x+=a.x;
	y+=a.y;
    }
    public void soustrait(point a){
	x-=a.x;
	y-=a.y;
    }
    public void soustrait(float xx,float yy){
	x-=xx;
	y-=yy;
    }
    public void additionne_facteur(point a,float b){
	x+=b*a.x;
	y+=b*a.y;
    }
    public void soustrait_facteur(point a,float b){
	x-=b*a.x;
	y-=b*a.y;
    }
    public float distance(point pt){
	float d;
	d=(float)Math.sqrt((float)Math.pow(x-pt.x,2)+(float)Math.pow(y-pt.y,2));
	return d;
    }
    public float longueur(){
	return ((float)Math.sqrt((float)Math.pow(x,2)+(float)Math.pow(y,2)));
    }
    public float longueur_carre(){
	return ((float)Math.pow(x,2)+(float)Math.pow(y,2));
    }
    public float scalaire(point a){
	return x*a.x+y*a.y;
    }
    public float produit_vectoriel(point a){
	return x*a.y-y*a.x;
    }
    public void rotation(float angle){
	float cos=(float)Math.cos(angle);float sin=(float)Math.sin(angle);
	float x_p=x;float y_p=y;
	x=cos*x_p-sin*y_p;
	y=sin*x_p+cos*y_p;
    }
    public void rotation(float c_ang,float s_ang){
	float x_p=x;float y_p=y;
	x=c_ang*x_p-s_ang*y_p;
	y=s_ang*x_p+c_ang*y_p;
    }
    public void print(String st){
	float xx=(float)x;float yy=(float)y;float l=(float)longueur();
	System.out.println(st+ " x "+xx+" y "+yy+" longueur() "+l);
    }
}
class matrice{
    float directe[][]=new float [64][64];
    float essai[][]=new float [64][64];
    float inverse[][]=new float [64][64];
    int dim;
    public matrice(int dim1){
	dim=dim1;
    }
    public void somme_lignes(){
	for (int k=0;k<dim;k++){
	    float somme=0;
	    for (int j=0;j<dim;j++)
		somme+=directe[k][j];
	    System.out.println("k "+k+" somme "+somme);
	}
    }
    public void invers ()
    {
	float pivot, souspiv;
	for (int i=0;i<dim;i++)
	    for (int j=0;j<dim;j++){
		essai[i][j]=directe[i][j]; 
		if (i==j)
		    inverse[i][j]=(float)1.0;
		else
		    inverse[i][j]=0;
	    }
	for (int i=0;i<dim;i++){
	    pivot=essai[i][i];
	    for (int j=i;j<dim;j++)
		essai[i][j]=essai[i][j]/pivot;
	    for (int j=0;j<=i;j++)
		inverse[i][j]=inverse[i][j]/pivot;
	    System.out.println("i "+i+" pivot "+pivot+" essai[i][i] "+essai[i][i]);
	    if(i>26)
		for (int j=i;j<dim;j++)
		    System.out.println("j "+j+" "+essai[j][24]+" "+essai[j][25]+" "+essai[j][26]+" "+essai[j][27]+" "+essai[j][28]+" "+essai[j][29]+" "+essai[j][30]+" "+essai[j][63]);
	    for (int ii=0;ii<dim;ii++){
		if (ii!=i) {
		    souspiv=essai[ii][i];
		    for (int j=i;j<dim;j++)
			essai[ii][j]-=essai[i][j]*souspiv;
		    for (int j=0;j<=i;j++)
			inverse[ii][j]-=inverse[i][j]*souspiv;
		}
	    }
	    //if(i>26)
	    //	for (j=i;j<dim;j++)
	    //    System.out.println("***j "+j+" "+essai[j][24]+" "+essai[j][25]+" "+essai[j][26]+" "+essai[j][27]+" "+essai[j][28]+" "+essai[j][29]+" "+essai[j][30]+" "+essai[j][63]);
	    
	}
	System.out.println( inverse[0][ 0]+" "+ inverse[1][ 1]+" "+ inverse[2][ 2]+" "+ inverse[3][ 3]+" "+ inverse[4][ 4]+" "+ inverse[5][ 5]+" "+ inverse[6][ 6]+" "+ inverse[7][ 7]);
	//inverse[7][1000]=0;
	/*
	  for (k=0;k<dim;k++){
	  for (j=0;j<dim;j++){
	  essai[k][j]=0;
	  for (int l=0;l<dim;l++)
	  essai[k][j]+=directe[k][l]*inverse[l][j];
	  }
	  System.out.println("k "+k+" "+essai[k][0]+" "+essai[k][1]+" "+essai[k][2]+" "+essai[k][3]+" "+essai[k][4]+" "+essai[k][5]+" "+essai[k][6]+" "+essai[k][7]);
	  }
	*/
    }
}


//class ensemble_de_cylindres extends Frame

