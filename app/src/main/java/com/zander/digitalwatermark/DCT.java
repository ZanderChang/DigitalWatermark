package com.zander.digitalwatermark;

public class DCT
{  
    /** 
     * DCT Block Size - default 8 
     * �ֿ��С
     */  
    public int N        = 8;  
  
    /** 
     * Image Quality (0-25) - default 25 (worst image / best compression) 
     */  
    public int QUALITY  = 25;  
  
    /** 
     * Image width - must correspond to imageArray bounds - default 320 
     */  
    public int ROWS     = 320;  
  
    /** 
     * Image height - must correspond to imageArray bounds - default 200 
     */  
    public int COLS     = 240;  
  
    /** 
     * The ZigZag matrix. 
     */  
    public int zigZag[][] = new int[64][2];  
  
    /** 
     * Cosine matrix. N * N. 
     */  
    public double c[][]        = new double[N][N];  
  
    /** 
     * Transformed cosine matrix, N*N. 
     */  
    public double cT[][]       = new double[N][N];  
  
    /** 
     * Quantitization Matrix. 
     */  
    public int quantum[][]     = new int[N][N];  
  
    /** 
     * DCT Result Matrix 
     */  
    public int resultDCT[][] = new int[ROWS][COLS];  
  
    /** 
     * Constructs a new DCT object. Initializes the cosine transform matrix 
     * these are used when computing the DCT and it's inverse. This also 
     * initializes the run length counters and the ZigZag sequence. Note that 
     * the image quality can be worse than 25 however the image will be 
     * extemely pixelated, usually to a block size of N. 
     * 
     * @param QUALITY The quality of the image (0 best - 25 worst) 
     * 
     */  
    
    

    
    public DCT(int QUALITY)  
    {  
        initZigZag();  
        initMatrix(QUALITY);  
//        int [] temp = {
//        		8 ,6 ,5, 8 ,12 ,20, 26, 30,6 
//        		,6 ,7 ,10, 13 ,29 ,30, 28,7,
//        		7 ,8 ,12, 20, 29 ,35 ,28,7, 
//        		9, 11 ,15, 26, 44 ,40 ,3, 19 
//        		,11 ,19 ,28, 34 ,55, 52,39,12,
//        		18, 28, 32, 41 ,52 ,57, 46,25 ,
//        		32 ,39, 44, 52, 61,60 ,51,36, 
//        		46 ,48 ,49, 56, 50, 52, 50
//        		};
//        
//        for(int i=0,k=0;i<8;++i)
//        	for(int j=0;j<8;++j)
//        		quantum[i][j] = temp[k++];
    }  
  
  
    /** 
     * ����������������������������
     * This method sets up the quantization matrix using the Quality parameter 
     * and then sets up the Cosine Transform Matrix and the Transposed CT. 
     * These are used by the forward and inverse DCT. The RLE encoding 
     * variables are set up to track the number of consecutive zero values 
     * that have output or will be input. 
     * @param quality The quality scaling factor 
     */  
    private void initMatrix(int quality)  
    {  
        int i;  
        int j;  
  
        for (i = 0; i < N; i++)  
        {  
            for (j = 0; j < N; j++)  
            {  
                quantum[i][j] = (1 + ((1 + i + j) * quality));  
            }  
        }  
        
        for (j = 0; j < N; j++)  
        {  
            double nn = (double)(N);  
            c[0][j]  = 1.0 / Math.sqrt(nn);  
            cT[j][0] = c[0][j];  
        }  
  
        for (i = 1; i < 8; i++)  
        {  
            for (j = 0; j < 8; j++)  
            {  
                double jj = (double)j;  
                double ii = (double)i;  
                c[i][j]  = Math.sqrt(2.0/8.0) * Math.cos(((2.0 * jj + 1.0) * ii * Math.PI) / (2.0 * 8.0));  
                cT[j][i] = c[i][j];  
            }  
        }  
    }  
  
    /** 
     * Initializes the ZigZag matrix. 
     */  
    private void initZigZag()  
    {  
        zigZag[0][0] = 0; // 0,0  
        zigZag[0][1] = 0;  
        zigZag[1][0] = 0; // 0,1  
        zigZag[1][1] = 1;  
        zigZag[2][0] = 1; // 1,0  
        zigZag[2][1] = 0;  
        zigZag[3][0] = 2; // 2,0  
        zigZag[3][1] = 0;  
        zigZag[4][0] = 1; // 1,1  
        zigZag[4][1] = 1;  
        zigZag[5][0] = 0; // 0,2  
        zigZag[5][1] = 2;  
        zigZag[6][0] = 0; // 0,3  
        zigZag[6][1] = 3;  
        zigZag[7][0] = 1; // 1,2  
        zigZag[7][1] = 2;  
        zigZag[8][0] = 2; // 2,1  
        zigZag[8][1] = 1;  
        zigZag[9][0] = 3; // 3,0  
        zigZag[9][1] = 0;  
        zigZag[10][0] = 4; // 4,0  
        zigZag[10][1] = 0;  
        zigZag[11][0] = 3; // 3,1  
        zigZag[11][1] = 1;  
        zigZag[12][0] = 2; // 2,2  
        zigZag[12][1] = 2;  
        zigZag[13][0] = 1; // 1,3  
        zigZag[13][1] = 3;  
        zigZag[14][0] = 0; // 0,4  
        zigZag[14][1] = 4;  
        zigZag[15][0] = 0; // 0,5  
        zigZag[15][1] = 5;  
        zigZag[16][0] = 1; // 1,4  
        zigZag[16][1] = 4;  
        zigZag[17][0] = 2; // 2,3  
        zigZag[17][1] = 3;  
        zigZag[18][0] = 3; // 3,2  
        zigZag[18][1] = 2;  
        zigZag[19][0] = 4; // 4,1  
        zigZag[19][1] = 1;  
        zigZag[20][0] = 5; // 5,0  
        zigZag[20][1] = 0;  
        zigZag[21][0] = 6; // 6,0  
        zigZag[21][1] = 0;  
        zigZag[22][0] = 5; // 5,1  
        zigZag[22][1] = 1;  
        zigZag[23][0] = 4; // 4,2  
        zigZag[23][1] = 2;  
        zigZag[24][0] = 3; // 3,3  
        zigZag[24][1] = 3;  
        zigZag[25][0] = 2; // 2,4  
        zigZag[25][1] = 4;  
        zigZag[26][0] = 1; // 1,5  
        zigZag[26][1] = 5;  
        zigZag[27][0] = 0; // 0,6  
        zigZag[27][1] = 6;  
        zigZag[28][0] = 0; // 0,7  
        zigZag[28][1] = 7;  
        zigZag[29][0] = 1; // 1,6  
        zigZag[29][1] = 6;  
        zigZag[30][0] = 2; // 2,5  
        zigZag[30][1] = 5;  
        zigZag[31][0] = 3; // 3,4  
        zigZag[31][1] = 4;  
        zigZag[32][0] = 4; // 4,3  
        zigZag[32][1] = 3;  
        zigZag[33][0] = 5; // 5,2  
        zigZag[33][1] = 2;  
        zigZag[34][0] = 6; // 6,1  
        zigZag[34][1] = 1;  
        zigZag[35][0] = 7; // 7,0  
        zigZag[35][1] = 0;  
        zigZag[36][0] = 7; // 7,1  
        zigZag[36][1] = 1;  
        zigZag[37][0] = 6; // 6,2  
        zigZag[37][1] = 2;  
        zigZag[38][0] = 5; // 5,3  
        zigZag[38][1] = 3;  
        zigZag[39][0] = 4; // 4,4  
        zigZag[39][1] = 4;  
        zigZag[40][0] = 3; // 3,5  
        zigZag[40][1] = 5;  
        zigZag[41][0] = 2; // 2,6  
        zigZag[41][1] = 6;  
        zigZag[42][0] = 1; // 1,7  
        zigZag[42][1] = 7;  
        zigZag[43][0] = 2; // 2,7  
        zigZag[43][1] = 7;  
        zigZag[44][0] = 3; // 3,6  
        zigZag[44][1] = 6;  
        zigZag[45][0] = 4; // 4,5  
        zigZag[45][1] = 5;  
        zigZag[46][0] = 5; // 5,4  
        zigZag[46][1] = 4;  
        zigZag[47][0] = 6; // 6,3  
        zigZag[47][1] = 3;  
        zigZag[48][0] = 7; // 7,2  
        zigZag[48][1] = 2;  
        zigZag[49][0] = 7; // 7,3  
        zigZag[49][1] = 3;  
        zigZag[50][0] = 6; // 6,4  
        zigZag[50][1] = 4;  
        zigZag[51][0] = 5; // 5,5  
        zigZag[51][1] = 5;  
        zigZag[52][0] = 4; // 4,6  
        zigZag[52][1] = 6;  
        zigZag[53][0] = 3; // 3,7  
        zigZag[53][1] = 7;  
        zigZag[54][0] = 4; // 4,7  
        zigZag[54][1] = 7;  
        zigZag[55][0] = 5; // 5,6  
        zigZag[55][1] = 6;  
        zigZag[56][0] = 6; // 6,5  
        zigZag[56][1] = 5;  
        zigZag[57][0] = 7; // 7,4  
        zigZag[57][1] = 4;  
        zigZag[58][0] = 7; // 7,5  
        zigZag[58][1] = 5;  
        zigZag[59][0] = 6; // 6,6  
        zigZag[59][1] = 6;  
        zigZag[60][0] = 5; // 5,7  
        zigZag[60][1] = 7;  
        zigZag[61][0] = 6; // 6,7  
        zigZag[61][1] = 7;  
        zigZag[62][0] = 7; // 7,6  
        zigZag[62][1] = 6;  
        zigZag[63][0] = 7; // 7,7  
        zigZag[63][1] = 7;  
    }  
  
    /** 
     * This method preforms a matrix multiplication of the input pixel data matrix 
     * by the transposed cosine matrix and store the result in a temporary 
     * N * N matrix. This N * N matrix is then multiplied by the cosine matrix 
     * and the result is stored in the output matrix. 
     * 
     * @param input The Input Pixel Matrix 
     * @returns output The DCT Result Matrix 
     */  
    public int[][] forwardDCT(int input[][])  
    {  
        int output[][] = new int[N][N];  
        double temp[][] = new double[N][N];  
        double temp1;  
        int i;  
        int j;  
        int k;  
  
        for (i = 0; i < N; i++)  
        {  
            for (j = 0; j < N; j++)  
            {  
                temp[i][j] = 0.0;  
                for (k = 0; k < N; k++)  
                {  
                    temp[i][j] += (((int)(input[i][k]) - 128) * cT[k][j]);  
                }  
            }  
        }  
  
        for (i = 0; i < N; i++)  
        {  
            for (j = 0; j < N; j++)  
            {  
                temp1 = 0.0;  
  
                for (k = 0; k < N; k++)  
                {  
                    temp1 += (c[i][k] * temp[k][j]);  
                }  
  
                output[i][j] = (int)Math.round(temp1);  
            }  
        }  
  
        return output;  
    }  
  
    /** 
     * This method reads in DCT codes  dequanitizes them 
     * and places them in the correct location. The codes are stored in the 
     * zigzag format so they need to be redirected to a N * N block through 
     * simple table lookup. After dequantitization the data needs to be 
     * run through an inverse DCT. 
     * 
     * @param inputData 8x8 Array of quantitized image data 
     * @param zigzag Boolean switch to enable/disable zigzag path. 
     * @returns outputData A N * N array of de-quantitized data 
     * 
     */  
    public int[][] dequantitizeImage(int[][] inputData, boolean zigzag)  
    {  
        int i = 0;  
        int j = 0;  
        int row;  
        int col;  
        int outputData[][] = new int[N][N];  
  
        double result;  
  
        if (zigzag)  
        {  
            for (i=0; i<(N*N); i++)  
            {  
                row = zigZag[i][0];  
                col = zigZag[i][1];  
  
                result = inputData[row][col] * quantum[row][col];  
                outputData[row][col] = (int)(Math.round(result));  
            }  
        }  
  
        else  
        {  
            for (i=0; i<8; i++)  
            {  
                for (j=0; j<8; j++)  
                {  
                    result = inputData[i][j] * quantum[i][j];  
                    outputData[i][j] = (int)(Math.round(result));  
                }  
            }  
        }  
  
        return outputData;  
    }  
  
    /** 
     * This method orders the DCT result matrix into a zigzag pattern and then 
     * quantitizes the data. The quantitized value is rounded to the nearest integer. 
     * Pixels which round or divide to zero are the loss associated with 
     * quantitizing the image. These pixels do not display in the AWT. (null) 
     * Long runs of zeros and the small ints produced through this technique 
     * are responsible for the small image sizes. For block sizes < or > 8, 
     * disable the zigzag optimization. If zigzag is disabled on encode it 
     * must be disabled on decode as well. 
     * 
     * @param result2 8x8 array of DCT image data. 
     * @param zigzag Boolean switch to enable/disable zigzag path. 
     * @returns outputData The quantitized output data 
     */  
    public int[][] quantitizeImage(double[][] result2, boolean zigzag)  
    {  
        int outputData[][] = new int[N][N];  
        int i = 0;  
        int j = 0;  
        int row;  
        int col;  
  
        double result;  
  
        if (zigzag)  
        {  
            for (i = 0; i < (N*N); i++)  
            {  
                row = zigZag[i][0];  
                col = zigZag[i][1];  
                result = (result2[row][col] / quantum[row][col]);  
                outputData[row][col] = (int)(Math.round(result));  
            }  
  
        }  
  
        else  
        {  
            for (i=0; i<N; i++)  
            {  
                for (j=0; j<N; j++)  
                {  
                    result = result2[i][j] / quantum[i][j];  
                    outputData[i][j] = (int)(Math.round(result));  
                }  
            }  
        }  
  
        return outputData;  
    }  
  
    /** 
     * <br><br> 
     * This does not huffman code, 
     * it uses a minimal run-length encoding scheme. Huffman, Adaptive Huffman 
     * or arithmetic encoding will give much better preformance. The information 
     * accepted is quantitized DCT data. The output array should be 
     * scanned to determine where the end is. 
     * 
     * @param image Quantitized image data. 
     * @returns The string representation of the image. (Compressed) 
     */  
    public int[] compressImage(int[] QDCT, boolean log)  
    {  
        int i = 0;  
        int j = 0;  
        int k = 0;  
        int temp = 0;  
        @SuppressWarnings("unused")  
        int curPos = 0;  
        int runCounter = 0;  
        int imageLength = ROWS*COLS;  
  
        int pixel[] = new int[ROWS*COLS];  
  
        while((i<imageLength))  
        {  
            temp = QDCT[i];  
  
            while((i < imageLength) && (temp == QDCT[i]))  
            {  
                runCounter++;  
                i++;  
            }  
  
            if (runCounter > 4)  
            {  
                pixel[j] = 255;  
                j++;  
                pixel[j] = temp;  
                j++;  
                pixel[j] = runCounter;  
                j++;  
            }  
            else  
            {  
                for (k=0; k<runCounter; k++)  
                {  
                    pixel[j] = temp;  
                    j++;  
                }  
            }  
  
            if (log)  
            {  
                System.out.print("." + "\r");  
            }  
  
            runCounter = 0;  
            //i++;  
        }  
  
        return pixel;  
    }  
  
    /** 
     * This method determines the runs in the input data, decodes it 
     * and then returnss the corrected matrix. It is used to decode the data 
     * from the compressImage method. Huffman encoding, Adaptive Huffman or 
     * Arithmetic will give much better compression. 
     * 
     * @param DCT Compressed DCT int array (Expands to whole image). 
     * @returns The decompressed one dimensional array. 
     */  
    public int[] decompressImage(int[] DCT, boolean log)  
    {  
        int i = 0;  
        int j = 0;  
        int k = 0;  
        int temp = 0;  
        int imageLength = ROWS*COLS;  
        int pixel[] = new int[ROWS*COLS];  
  
        while (i < imageLength)  
        {  
            temp = DCT[i];  
  
            if (k < imageLength)  
            {  
                if (temp == 255)  
                {  
                    i++;  
                    int value = DCT[i];  
                    i++;  
                    int length = DCT[i];  
  
                    for(j=0; j<length; j++)  
                    {  
                        pixel[k] = value;  
                        k++;  
                    }  
                }  
  
                else  
                {  
                    pixel[k] = temp;  
                    k++;  
                }  
            }  
            if (log)  
            {  
                System.out.print(".." + "\r");  
            }  
  
            i++;  
        }  
  
        for (int a = 0; a < 80; a++)  
        {  
            System.out.print(pixel[a] + " ");  
        }  
        System.out.println();  
        for (int a = 0; a < 80; a++)  
        {  
            System.out.print(DCT[a] + " ");  
        }  
  
        return pixel;  
    }  
  
    /** 
     * This method is preformed using the reverse of the operations preformed in 
     * the DCT. This restores a N * N input block to the corresponding output 
     * block with values scaled to 0 to 255 and then stored in the input block 
     * of pixels. 
     * 
     * @param input N * N input block 
     * @returns output The pixel array output 
     */  
    public int[][] inverseDCT(int input[][])  
    {  
        int output[][] = new int[N][N];  
        double temp[][] = new double[N][N];  
        double temp1;  
        int i;  
        int j;  
        int k;  
  
        for (i=0; i<N; i++)  
        {  
            for (j=0; j<N; j++)  
            {  
                temp[i][j] = 0.0;  
  
                for (k=0; k<N; k++)  
                {  
                    temp[i][j] += input[i][k] * c[k][j];  
                }  
            }  
        }  
  
        for (i=0; i<N; i++)  
        {  
            for (j=0; j<N; j++)  
            {  
                temp1 = 0.0;  
  
                for (k=0; k<N; k++)  
                {  
                    temp1 += cT[i][k] * temp[k][j];  
                }  
  
                temp1 += 128.0;  
  
                if (temp1 < 0)  
                {  
                    output[i][j] = 0;  
                }  
                else if (temp1 > 255)  
                {  
                    output[i][j] = 255;  
                }  
                else  
                {  
                     output[i][j] = (int)Math.round(temp1);  
                }  
            }  
        }  
  
        return output;  
    }  
  
    /** 
     * This method uses bit shifting to convert an array of two bytes 
     * to a integer (16 bits max). Byte 0 is the most signifigant byte 
     * and Byte 1 is the least signifigant byte. 
     * @param bitSet Two bytes to convert 
     * @returns The constructed integer 
     */  
    @SuppressWarnings("unused")  
    private int bytetoInt(byte bitSet[])  
    {  
        int returnInt = 0;  
  
        byte MSB = bitSet[0];  
        byte LSB = bitSet[1];  
  
        returnInt = ((MSB+128) << 8) | (LSB+128);  
  
        return returnInt;  
    }  
  
    /** 
     * This method uses bit shifting to convert an integer to an array 
     * of two bytes, byte 0, the most signifigant and byte 1, the least 
     * signifigant. 
     * @param count The integer to convert. (16 bit max) 
     * @returns The array of two bytes. 
     */  
    @SuppressWarnings("unused")  
    private byte[] inttoByte(int count)  
    {  
        int LSB = 0;  
        int MSB = 0;  
        byte bitSet[] = new byte[2];  
  
        if (!(count > 65535))  
        {  
            LSB = ((count & 0x000000ff));  
            MSB = ((count & 0x0000ff00) >> 8);  
        }  
        else  
        {  
            System.out.println("Integer > than 16 bit. Exiting..");  
            System.exit(count);  
        }  
  
        bitSet[0] = (byte)(MSB-128);  
        bitSet[1] = (byte)(LSB-128);  
  
        return bitSet;  
    }
    

    
    
}
