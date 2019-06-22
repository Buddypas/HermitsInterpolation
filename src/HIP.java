import java.util.Scanner;

public class HIP {

	public static Scanner input = new Scanner(System.in);
	public int m;
	public double z;
	public double[] x;
	public double[] f;
	public int[] k;
	public double[][] izvodi;
	
	public HIP() {
		System.out.println("Unesi trazenu tacku: ");
		String tempString = input.nextLine();
		this.z = Double.parseDouble(tempString);
		
		System.out.println("Unesi broj cvorova: ");
		tempString = input.nextLine();
		this.m = Integer.parseInt(tempString);//broj cvorova interpolacije
		
		this.x = new double[m];//niz cvorova interpolacije
		for(int i=0; i < m; i++)
		{
			System.out.println("Unesi " + i + ". cvor: ");
			tempString = input.nextLine();
			x[i] = Double.parseDouble(tempString);
		}
		
		f = new double[m];//vrijednosti funkcije u cvorovima
		for(int i=0;i<m;i++)
		{
			System.out.println("Unesi vrijednost funkcije u " + i + ". cvoru: ");
			tempString = input.nextLine();
			f[i]=Double.parseDouble(tempString);
		}
		
		k = new int[m];//niz u kojem je i-ti clan broj poznatih izvoda i-tog cvora interpolacije
		for(int i=0;i<m;i++)
		{
			System.out.println("Unesi broj poznatih izvoda za " + i + ". cvor: ");
			tempString = input.nextLine();
			k[i]=Integer.parseInt(tempString);
		}
		
		int maxIzvod = maxNiza(k);//maksimalni izvod od svih cvorova
		izvodi = new double[m][maxIzvod];
		for(int i=0;i<m;i++)
		{
			for(int j=0;j<maxIzvod;j++)
			{
				System.out.println("Unesi " + (j+1) + ". izvod funkcije u " + i + ". cvoru: ");
				tempString = input.nextLine();
				izvodi[i][j]=Double.parseDouble(tempString);
			}
		}
		
		int[] n = new int[m];//niz u kojem je i-ti clan visestrukost i-tog cvora interpolacije u tabeli podijeljenih razlika
		for(int i = 0; i < m; i++)
			n[i] = k[i] + 1;
		int N = sumaNiza(n)-1;//broj kolona tabele podijeljenih razlika (ne odnosi se na tabelu cvorova i vrijednosti funkcije)
		double[] xZaTabelu = pocniTabelu(N,x,n);//cvorovi sa svojim visestrukostima za tabelu
		double[] fZaTabelu = pocniTabelu(N,f,n);//vrijednosti u cvorovima sa visestrukostima za tabelu
		double[][] tabelaPodijeljenihRazlika = popuniTabelu(N,x,xZaTabelu,fZaTabelu,izvodi);
		double rjesenje = Polinom(z,tabelaPodijeljenihRazlika,N,n,x);
		System.out.println(rjesenje);
	}
	
	public static void stampaNiza(double[] niz)
	{
		for(int i=0;i<niz.length;i++)
			System.out.print(niz[i] + " ");
	}
	
	public static int maxNiza(int[] niz)
	{
		int max = niz[0];
		for(int i=0;i<niz.length;i++)
		{
			if(max<niz[i]) max=niz[i];
		}
		return max;
	}
	
	public static int faktorijel(int n)
	{
		if(n==1||n==0) return 1;
		return n*faktorijel(n-1);
	}
	
	public static int sumaNiza(int[] niz)
	{
		int suma=0;
		for(int i=0;i<niz.length;i++)
			suma+=niz[i];
		return suma;
	}
	
	public static int nadjiPoVrijednosti(double[] niz, double x)
	{
		int rez=0;
		for(int i=0;i<niz.length;i++)
		{
			if(niz[i]==x) 
			{
				rez=i;
				break;
			}
		}
		return rez;
	}
	
	public static double[] pocniTabelu(int N, double[] xf,int[] n)
	{
		double[] xfZaTabelu = new double[N+1];
		int i=0;
		int j=0;
		while(j<N+1)
		{
			for(int l=0;l<n[i];l++)
			{
				xfZaTabelu[j]=xf[i];
				j++;
			}
			i++;
		}
		return xfZaTabelu;
	}
	
	public static double[][] popuniTabelu(int N,double[] x,double[] xZaTabelu,double[] fZaTabelu,double[][] izvodi)
	{
		double[][] tabelaPodijeljenihRazlika = new double [N+1][N+2];
		for(int i = 0;i<N+1;i++)
		{
			tabelaPodijeljenihRazlika[i][0]=xZaTabelu[i];
			tabelaPodijeljenihRazlika[i][1]=fZaTabelu[i];
		}
		for(int j=2;j<N+2;j++)
		{
			for(int i=0;i<N;i++)
			{
				if(i+j-1>=N+1) break;
				else
				{
					if(tabelaPodijeljenihRazlika[i][0]==tabelaPodijeljenihRazlika[i+j-1][0])
					{
						if(j-2<izvodi[0].length)
						{
							int index=nadjiPoVrijednosti(x, xZaTabelu[i]);//index od xi u tabeli podijeljenih razlika u nizu cvorova
							tabelaPodijeljenihRazlika[i][j]=izvodi[index][j-2]/(double)faktorijel(j-1);
						}
					}
					else tabelaPodijeljenihRazlika[i][j]=(tabelaPodijeljenihRazlika[i+1][j-1]-tabelaPodijeljenihRazlika[i][j-1])/(tabelaPodijeljenihRazlika[i+j-1][0]-tabelaPodijeljenihRazlika[i][0]);
				}
			}
		}
		return tabelaPodijeljenihRazlika;
	}
	
	public static void stampajTabelu(double[][] tabela)
	{
		for(int i=0;i<tabela.length;i++)
		{
			for(int j = 0;j<tabela[0].length;j++)
				System.out.print(tabela[i][j] + "\t");
			System.out.println();
		}
	}
	
	public static double Polinom(double z,double[][] tabelaPodijeljenihRazlika,int N,int[] n,double[] x)
	{
		stampajTabelu(tabelaPodijeljenihRazlika);
		double p=1;
		int brZaKoef=2;
		double s=tabelaPodijeljenihRazlika[0][1];
		double koef;
		int i=0;
		while(brZaKoef<N+2)
		{
			int visestrukost = 0;
			while(visestrukost<n[i])
			{
				if(brZaKoef>=N+2) break;
				koef=tabelaPodijeljenihRazlika[0][brZaKoef];
				p=p*(z-x[i]);
				s=s+p*koef;
				brZaKoef++;
				visestrukost++;
			}
			i++;
		}
		return s;
	}
	
	public static void main(String[] args) {
		HIP program = new HIP();
		
	}
}
