package com.zander.digitalwatermark;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class HideW extends Activity{
    public  int [][] pixel_r;
    
    public  int [][] pixel_g;
    
    public  int [][] pixel_b;
    
    public int [][] pixel_a;
    
    public float[][] matrix_M;//�洢RGB֮��ı���
    public float[][] matrix_N;    
    
    public double[][] matrix_ld;
    public DCT dct;
    
    public Bitmap btmp;
    
    public int width;
    public int height;
    public int need_size;
    
    public HideW(int quality,int watersz){
    	dct = new DCT(quality);
    	need_size = watersz*8;
    }
    
    public void detachRGB(Bitmap image){
    	int w = image.getWidth();
    	int h = image.getHeight();
    	width = w/2-need_size/2;//########
    	height = h/2-need_size/2;
    	Log.d("debug", "w="+w+"h="+h);
    	
    	pixel_a = new int[need_size][need_size];
    	pixel_r = new int[need_size][need_size];
    	pixel_g = new int[need_size][need_size];
    	pixel_b = new int[need_size][need_size];
//    	matrix_M = new float[need_size][need_size];
//    	matrix_N = new float[need_size][need_size];
//    	matrix_ld = new double[need_size][need_size];
//    	
    	int pixel = 0;
    	int i,j;
//		int max_h = height + need_size;
//		int max_w = width+  need_size;
    	for(i = 0; i < need_size; ++i) {
			for(j = 0; j < need_size; ++j) {
				pixel = image.getPixel(j + width, i + height); //########
				pixel_a[i][j] = Color.alpha(pixel);
				pixel_r[i][j] = Color.red(pixel);
				pixel_g[i][j] = Color.green(pixel);
				pixel_b[i][j] = Color.blue(pixel);

//    			if(pixel_r[i][j]==0){
//    				matrix_M[i][j] = pixel_g[i][j];
//    				matrix_N[i][j] = pixel_b[i][j];
//    			}
//    			else{
//    				matrix_M[i][j] = pixel_g[i][j]/(float)pixel_r[i][j];
//    				matrix_N[i][j] = pixel_b[i][j]/(float)pixel_r[i][j];
//    			}
//    			matrix_ld[i][j] =  (0.2989*pixel_r[i][j]+0.5870*pixel_g[i][j]+0.1140*pixel_b[i][j]);
//    			//Log.d("debug","i="+i+"j="+j);
			}
		}
    }
    public Bitmap mixRGB(){
    	int h = btmp.getHeight();
    	int w =  btmp.getWidth();
    	//k=0;
    	//��ԭRGB
    	
//    	for(int i=0;i<need_size;++i)
//    		for(int j=0;j<need_size;++j)
//    		{
//    			double sum = 0.2989+0.5870*matrix_M[i][j]+0.1140*matrix_N[i][j];
//    			pixel_r[i][j] = (int) (matrix_ld[i][j]/sum);
//    			pixel_g[i][j] = (int) (matrix_ld[i][j]*matrix_M[i][j]/sum);
//    			pixel_b[i][j] = (int) (matrix_ld[i][j]*matrix_N[i][j]/sum);
//    		}
    	
    	//Color color = new Color();
//    	Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//    	for(int i=0;i<h;++i)
//    		for(int j=0;j<w;++j){
//    			int c =btmp.getPixel(j, i);
//    			bitmap.setPixel(j,i, c);
//    			//Log.d("debug","mix i="+i+"j="+j);
//    		}
		Bitmap bitmap = btmp.copy(btmp.getConfig(), true);
//    	int max_h = need_size+height;
//    	int max_w = need_size+width;
//		Bitmap bitmap = Bitmap.
    	for(int i=0;i<need_size;++i)
    		for (int j = 0;j<need_size;++j){
    			int c = Color.argb(pixel_a[i][j], pixel_r[i][j], pixel_g[i][j], pixel_b[i][j]);
    			bitmap.setPixel(j+width,i+height, c);
    		}
    	return bitmap;
    }
    public double texture(int [][]input){
		int N = input.length;
		double result=0;
		for(int i=0;i<N;++i)
			for(int j=0;j<N;++j)
				result+=input[i][j];
		result = result/(N*N);
		double temp = 0;
		for(int i=0;i<N;++i)
			for(int j=0;j<N;++j)
				temp+=(input[i][j]-result)*(input[i][j]-result);
		temp = Math.sqrt(temp)/(N*N);    	
    	return temp;
    }

    //��ˮӡ��ϢǶ�뵽����ͼƬ��
	public Bitmap hideinfo(Bitmap image,Bitmap watermark){
		int w_i,w_j;
		int image_w = image.getWidth();
		int image_h = image.getHeight();
		int water_w = watermark.getWidth();
		int water_h = watermark.getHeight();
		int pixel;
		
		StringBuilder sb = new StringBuilder();
		btmp = image;
		//��ˮӡ��Ԥ�������
		//��������ˮӡͼ�񣬵õ�ÿ������ֵ��32bit��Ϣ,��ƴ�ӵ�stringbuilder��
		for(w_i=0;w_i<water_h;w_i++)
			for(w_j=0;w_j<water_w;++w_j)
			{
				pixel  = watermark.getPixel(w_j, w_i);//##############���к���
				if(pixel==-1)
					sb.append('1');
				else
					sb.append('0');
				
			}
		//�ж�ͼƬ�Ƿ���������Ϣ��Ƕ�룬���ܣ���ҪRGB�еļ���������	
		String info = sb.toString();
		//Log.d("debug", info.substring(0, 100));
		//String info = "0011101011010100100000110101000011100101100";
//		int need_num=0;//��Ҫ�ķ�������RGB��
//		int total_bit = image_w*image_h/64;//ÿһ�������Ŀ���
		detachRGB(image);
		
		int i=0;
		int flag=0;
		int j,k,l,m;//������
		int info_position=0;//��¼���ǵ�ǰҪǶ���ˮӡ��λ��
		int[][] temp = new int[dct.N][dct.N];
		double [] temp1 = new double[dct.N*dct.N];
		double [] temp2 = new double[dct.N*dct.N];
		double [][]result = new double[dct.N][dct.N];
		double qi=0,temp3=0,temp4=0,q=5;

		for(j=0;j<need_size;j+=dct.N)
			for(k=0;k<need_size&&flag==0;k+=dct.N)
			{
				for(l=0;l<dct.N;++l)
					for(m=0;m<dct.N;++m)
						temp[l][m] = pixel_r[j+l][k+m]; // r -> bs

				//�õ�DCTϵ��
				temp = dct.forwardDCT(temp);
				//�õ�zigzag��ʽ��DCTϵ��
				for(l=0;l<64;++l){
					temp1[l] = temp[dct.zigZag[l][0]][dct.zigZag[l][1]];
					temp2[l] = dct.quantum[dct.zigZag[l][0]][dct.zigZag[l][1]];
					//Log.d("debug", ""+temp2[l]);
				}
				qi = 50 + 10*texture(temp); // key
				l=5;
				temp3 = temp1[l-2]+temp1[l-1]+temp1[l]+temp1[l+1]+temp1[l+2];
				if(info.charAt(info_position)=='0'){
					temp4 = temp3/5-qi;
					double r1 = Math.round(temp4/temp2[l])*temp2[l];
					double r2=0;
					for (m=l-2;m<l+3;++m)
						r2 += (Math.round(temp1[m]/temp2[m]))*temp2[m];
					r2 = r2/5-q;
					if(r1>=r2){
						while(r1>=r2){
							temp4 = temp4-1;
							qi++;
							r1 = Math.round(temp4/temp2[l])*temp2[l];
						}
					}
					temp1[l] = temp4;
				}
				else{
					temp4 = temp3/5+qi;
					double r1 = Math.round(temp4/temp2[l])*temp2[l];
					double r2=0;
					for (m=l-2;m<l+3;++m)
						r2 += (Math.round(temp1[m]/temp2[m]))*temp2[m];
					r2 = r2/5+q;
					if(r1<=r2){
						while(r1<=r2){
							temp4 = temp4+1;
							qi++;
							r1 = (double)((Math.round(temp4/temp2[l]))*temp2[l]);
							//Log.d("debug", "����У��2"+"temp4="+temp4+"temp2="+temp2[l]);
						}
					}
					temp1[l] = temp4;
				}
				++info_position;
				//Log.d("debug", "Ƕ��ɹ�");
				for(l=0;l<64;++l)//ͨ��zigzag�ķ�ʽ��һά����任��8x8����
				{
					result[dct.zigZag[l][0]][dct.zigZag[l][1]] = temp1[l];
				}
				
				temp = dct.quantitizeImage(result, true);//����
				temp = dct.inverseDCT(temp);
				
				for(l=0;l<dct.N;++l)
					for(m=0;m<dct.N;++m)
						pixel_r[j+l][k+m]=temp[l][m];//#####################
				
				
				if(info_position>=info.length()){//����ˮӡ�Ѿ�Ƕ����ϣ�flag��Ϊ1
					flag=1;
				}
			}
		return mixRGB();
	}
	
}
