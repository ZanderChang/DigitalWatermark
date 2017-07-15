package com.zander.digitalwatermark;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Take {
		public DCT dct;
		public int[][] Di;
		public int[]   wm;
		public int[]   twm;
		private static final int MID = 1;
		private static final int END = 2;
		public int resDCT[][] = new int[8][8];
		public int bit;//Ƕ��ˮӡ�ı߳�
		public int need_size;//Ƕ��ռ����Ĵ�С
		public int block1;
		
		private int[][] pixel_a;
		private int[][] pixel_r;
		private int[][] pixel_g;
		private int[][] pixel_b;
		private double[][] matrix_ld;
		
		public int w = 0, h = 0;
		
		public int size = 0;

	    public int width;
	    public int height;
	    
	    
	    public Take(int size,DCT dct1){
	    	dct = dct1;
	    	this.size = size;
	    	bit = size*size;
			wm  = new int[bit];
			need_size = 8*size;
			//twm  = new int[bit/32];
	    }
	    
	    
	    public void detachRGB(Bitmap image){
	    	int w = image.getWidth();
	    	int h = image.getHeight();
	    	width = w/2-need_size/2;
	    	height = h/2-need_size/2;
	    	
	    	pixel_a = new int[need_size][need_size];
	    	pixel_r = new int[need_size][need_size];
	    	pixel_g = new int[need_size][need_size];
	    	pixel_b = new int[need_size][need_size];
//	    	matrix_ld = new double[need_size][need_size];
	    	
	    	int pixel = 0;
	    	int i,j;
	    	for(i=0;i<need_size;++i){
				String n = "ffff";
				for(j=0;j<need_size;++j){
					pixel = image.getPixel(j+width, i+height);
					pixel_a[i][j] = Color.alpha(pixel);
					pixel_r[i][j] = Color.red(pixel);
					pixel_g[i][j] = Color.green(pixel);
					pixel_b[i][j] = Color.blue(pixel);

//	    			if(pixel_r[i][j]==0){
//	    				matrix_M[i][j] = pixel_g[i][j];
//	    				matrix_N[i][j] = pixel_b[i][j];
//	    			}
//	    			else{
//	    				matrix_M[i][j] = pixel_g[i][j]/(float)pixel_r[i][j];
//	    				matrix_N[i][j] = pixel_b[i][j]/(float)pixel_r[i][j];
//	    			}
//	    			matrix_ld[i][j] =  (0.2989*pixel_r[i][j]+0.5870*pixel_g[i][j]+0.1140*pixel_b[i][j]);
					Log.d("debug","i="+i+"j="+j);
				}
			}
	    }
		private void restoDi() {
			int status = 1;
			int block = 0;
			int i = 0;
			int j = 0;
			int pos = 0;
			int temp[][] = new int[8][8];
			int temp1[] = new int[64];


			int k,l,m;
			for(j=0;j<need_size;j+=dct.N) {
				for(k=0;k<need_size;k+=dct.N) {
					for(l=0;l<dct.N;++l)
						for(m=0;m<dct.N;++m)
							temp[l][m] = pixel_r[j+l][k+m]; // r -> b

					//�õ�DCTϵ��
					temp = dct.forwardDCT(temp);
					for(l=0;l<64;++l){
						temp1[l] = temp[dct.zigZag[l][0]][dct.zigZag[l][1]];
					}

					double sum = sum_Di(temp1);
					if(temp1[12] > sum){
						wm[pos] = 1;
					}
					else wm[pos] = 0;

					if(pos++ == (bit-1)){
						return;
					}
				}
			}
		}
		
		private double sum_Di(int[] array) {
			double sum = 0;
			int  l = 5;
			sum = (array[l-2] +array[l-1] +array[l] +array[l+1] +array[l+2])/5.0;
			
			return sum;
		}
		
		public Bitmap take_wm(Bitmap image) {
			int sumD;
			int pos;
			int[] temp1 = new int[64];
			
			//InputStream is = new 
			
			//Bitmap image = BitmapFactory.decodeStream(is);
			
			detachRGB(image);
			
			restoDi();
			
//			for(int i = 0; i < bit/32; ++i) {
//				for(int j = 0; j < 32; ++j) {
//					twm[i] = twm[i]|(wm[i*32+j] << (31-j));
//				}
//			}
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<100;++i)
				sb.append(wm[i]);
			Log.d("debug","��ȡ���="+sb.toString());
				
			Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
			for(int i=0;i<wm.length;++i)
			{
				if(wm[i]==1)
					bitmap.setPixel(i%size, (int)(i/size), Color.BLACK);
				//int temp = Color.argb(Color.alpha(twm[i]), Color.red(twm[i]), Color.green(twm[i]), Color.blue(twm[i]));
//				if(temp>0xFF000000+100)
//					bitmap.setPixel((int)(i/32),i%32,  Color.WHITE);
				//else
				else
					bitmap.setPixel(i%size, (int)(i/size), 	Color.WHITE);
			}
//			for(int i=0;i<wm.length;++i)
//			{
				
//				int temp = Color.argb(Color.alpha(twm[i]), Color.red(twm[i]), Color.green(twm[i]), Color.blue(twm[i]));
//				bitmap.setPixel((int)(i/32),i%32,  temp);
//
//			}
			
			return bitmap;
		}
}
