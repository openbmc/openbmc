/*
  gcc-6 memspeed.c cpuidc.c -lm -lrt -O3 -march=armv8-a -o memSpdPi64
  #define version "armv8 64 Bit"
*/

 #include <stdio.h>
 #include <stdlib.h>

 #include <string.h>
 #include "cpuidh.h"
 #include <math.h>
 #include <malloc.h>

//  #define Integer64Bits
  #define Integer32Bits

//  #define version  "64 Bit Version 4"
//  #define version  "32 Bit Version 4"
//  #define version  "vfpv4 32 Bit Version 1"
  #define version "armv8 64 Bit"

    double runSecs = 0.1;   
    int n1;

  double * xd;
  double * yd;
  float  * xs;
  float  * ys;

  #ifdef Integer64Bits
   long long  * xi;   
   long long  * yi;   
  #else
   int    * xi; 
   int    * yi;
  #endif


  void checkTime()
  {
      if (secs < runSecs)
     {
          if (secs < runSecs / 8.0)
          {
                n1 = n1 * 10;
          }
          else
          {
                n1 = (int)(runSecs * 1.25 / secs * (double)n1+1);
          }
      }
  }


int main(int argc, char *argv[])
{
    
    int passes[25];
    int allocMem[25];

    double  ramKB = 0.0;
    int useMem;   

  
    float  sums;
    float  ones = 1.0;
    float  zeros = 0;
      
    int g, i, m, j, nn;
    int kd, ks, ki, mem;

    #ifdef Integer64Bits
     long long  sumi;   
    #else
     int  sumi;
    #endif
   
    int  zeroi = 0;
    int onei = 1;
             
    int inc;
    double sumd, mbpsd, mbpss, mbpsi;
    double oned = 1.0;
    double zerod = 0;
    double memMB;
    
    int runs = 12; // 8 MB;
    int    param;

    if (argc > 2)
    {
       sscanf(argv[2], "%d", &param);
       if (param > 0)
       {
           if (param >  120) runs = 16;
           if (param >  250) runs = 17;
           if (param >  500) runs = 18;
           if (param > 1000) runs = 19;
       }
    }

    FILE    *outfile;
    
    outfile = fopen("memSpeed.txt","a+");
    if (outfile == NULL)
    {
        printf ("Cannot open results file \n\n");
        printf(" Press Enter\n");
        g  = getchar();
        exit (0);
    }
    printf("\n");
    getDetails();
 
    printf(" ##########################################\n"); 
    fprintf (outfile, " #####################################################\n\n");                     


//    ramKB = ramGB * 1000000;

    allocMem[0] = 2;  // KB two arrays
    for (i=1; i<23; i++)
    {
        allocMem[i] = allocMem[i-1] * 2;
    }


    passes[0] =      250;   //      4 KB
    passes[1] =      500;   //      8 KB
    passes[2] =     1000;   //     16 KB
    passes[3] =     2000;   //     32 KB
    passes[4] =     4000;   //     64 KB
    passes[5] =     8000;   //    128 KB
    passes[6] =    16000;   //    256 KB
    passes[7] =    32000;   //    512 KB
    passes[8] =    64000;   //    1 MB
    passes[9] =   128000;   //    2 MB
    passes[10] =  256000;   //    4 MB
    passes[11] =  512000;   //    8 MB
    passes[12] =  1024000;  //   16 MB
    passes[13] =  2048000;  //   32 MB
    passes[14] =  4096000;  //   64 MB
    passes[15] =  8192000;  //  128 MB
    passes[16] =  16384000; //  256 MB
    passes[17] =  32768000; //  512 MB
    passes[18] =  65536000; // 1024 MB
    passes[19] = 131072000; // 2048 MB
    passes[20] = 262144000; // 4096 MB
    passes[21] = 524288000; // 8192 MB
    passes[22] =1048576000; //16384 MB

    if (ramKB >    14000) runs = 11;
    if (ramKB >    30000) runs = 12;
    if (ramKB >    60000) runs = 13;
    if (ramKB >   120000) runs = 14;
    if (ramKB >   250000) runs = 15;
    if (ramKB >   500000) runs = 16;
    if (ramKB >  1000000) runs = 17;
    if (ramKB >  1500000) runs = 18;
    if (ramKB >  3500000) runs = 19;
    if (ramKB >  7500000) runs = 20;
    if (ramKB > 15500000) runs = 21;
    if (ramKB > 31500000) runs = 22;
    if (ramKB > 63500000) runs = 23;

    useMem = allocMem[runs - 1];

    xd = (double *)malloc(useMem * 1024);
    if (xd == NULL)
    {
       printf(" ERROR WILL EXIT\n");
       printf(" Press Enter\n");
       g  = getchar();
       exit(1);
    }

    yd = (double *)malloc(useMem * 1024);
    if (yd == NULL)
    {
       printf(" ERROR WILL EXIT\n");
       printf(" Press Enter\n");
       free(xd);
       g  = getchar();
       exit(1);
    }

    float *xs = (float *) xd;
    float *ys = (float *) yd;

    #ifdef Integer64Bits
     long long *xi = (long long *) xd;
     long long *yi = (long long *) yd;     
    #else
     int   *xi = (int *) xd;
     int   *yi = (int *) yd;     
    #endif

    printf ("\nFrom File /proc/cpuinfo\n");
    printf("%s\n", configdata[0]);
    printf ("\nFrom File /proc/version\n");
    printf("%s\n", configdata[1]);

    local_time();
    
    printf("\n   Memory Reading Speed Test %s %s ", version, timeday);
    printf("                Copyright (C) 2013, Roy Longbottom\n\n");
    printf("  Memory   x[m]=x[m]+s*y[m] Int  x[m]=x[m]+y[m]         x[m]=y[m]\n"); 

    fprintf(outfile, "     Memory Reading Speed Test %s by Roy Longbottom\n", version);
    fprintf(outfile, "\n               Start of test %s\n", timeday);
    fprintf(outfile, "  Memory   x[m]=x[m]+s*y[m] Int+   x[m]=x[m]+y[m]         x[m]=y[m]\n"); 

    #ifdef Integer64Bits
     printf("  KBytes    Dble   Sngl  Int64   Dble   Sngl  Int64   Dble   Sngl  Int64\n");   
     fprintf(outfile, "  KBytes    Dble   Sngl  Int64   Dble   Sngl  Int64   Dble   Sngl  Int64\n");     
    #else
     printf("  KBytes    Dble   Sngl  Int32   Dble   Sngl  Int32   Dble   Sngl  Int32\n");   
     fprintf(outfile, "  KBytes    Dble   Sngl  Int32   Dble   Sngl  Int32   Dble   Sngl  Int32\n");     
    #endif

    printf("    Used    MB/S   MB/S   MB/S   MB/S   MB/S   MB/S   MB/S   MB/S   MB/S\n\n");
    fprintf(outfile, "    Used    MB/S   MB/S   MB/S   MB/S   MB/S   MB/S   MB/S   MB/S   MB/S\n\n");
       
    for (j=1; j<runs; j++)
    {
        kd = passes[j];
        
        nn = 6400000 / kd;
        if (nn < 1) nn = 1;
        
        ks = kd * 2;

        #ifdef Integer64Bits   
         ki = kd; 
        #else
         ki = kd * 2; 
        #endif
        
        memMB = (double)kd * 16.0 / 1000000;
        mem = (int)((double)kd * 16.0 / 1000);

        inc = 4;
          
        n1 = nn;
        
        do
        {
            sumd = 1.00001;
            for (m=0; m<kd; m++)
            {
                  xd[m] = oned;
                  yd[m] = oned;
            }          
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<kd; m=m+inc)
                {
                   xd[m]   = xd[m]   + sumd * yd[m];
                   xd[m+1] = xd[m+1] + sumd * yd[m+1];
                   xd[m+2] = xd[m+2] + sumd * yd[m+2];
                   xd[m+3] = xd[m+3] + sumd * yd[m+3];
                 }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);
        
        mbpsd = (double)n1 * memMB / secs;
        printf("%8d %7d", mem, (int)mbpsd);

                 
        n1 = nn;
        do
        {
            sums = 1.0001;
            for (m=0; m<ks; m++)
            {
                  xs[m] = ones;
                  ys[m] = ones;
            }          
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<ks; m=m+inc)
                {
                   xs[m]   = xs[m]   + sums * ys[m];
                   xs[m+1] = xs[m+1] + sums * ys[m+1];
                   xs[m+2] = xs[m+2] + sums * ys[m+2];
                   xs[m+3] = xs[m+3] + sums * ys[m+3];
                }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);

        mbpss =  (double)n1 * memMB / secs;
        printf("%7d", (int)mbpss);
         
        n1 = nn;
        do
        {
            sumi = nn;
            for (m=0; m<ki; m++)
            {
                  xi[m] = zeroi;
                  yi[m] = zeroi;
            }          
            yi[ki-1] = onei;
                    
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
               for (m=0; m<ki; m=m+inc)
               {
                   xi[m]   = xi[m]   + sumi + yi[m];
                   xi[m+1] = xi[m+1] + sumi + yi[m+1];
                   xi[m+2] = xi[m+2] + sumi + yi[m+2];
                   xi[m+3] = xi[m+3] + sumi + yi[m+3];
                }
                sumi = -sumi;
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);
                  
        mbpsi = (double)n1 * memMB / secs;
        fprintf(outfile, "%8d %7d%7d%7d", mem, (int)mbpsd, (int)mbpss, (int)mbpsi);
        printf("%7d", (int)mbpsi);
            
        n1 = nn;
        do
        {
            for (m=0; m<kd; m++)
            {
                  xd[m] = zerod;
                  yd[m] = oned;
            }          
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<kd; m=m+inc)
                {
                      xd[m]   = xd[m] + yd[m];
                      xd[m+1] = xd[m+1] + yd[m+1];
                      xd[m+2] = xd[m+2] + yd[m+2];
                      xd[m+3] = xd[m+3] + yd[m+3];
                }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);
    
        sumd = xd[1];

        mbpsd = (double)n1 * memMB / secs;
        printf("%7d", (int)mbpsd);

        n1 = nn;
        do
        {
            for (m=0; m<ks; m++)
            {
                  xs[m] = zeros;
                  ys[m] = ones;
            }          
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<ks; m=m+inc)
                {
                      xs[m] = xs[m] + ys[m];
                      xs[m+1] = xs[m+1] + ys[m+1];
                      xs[m+2] = xs[m+2] + ys[m+2];
                      xs[m+3] = xs[m+3] + ys[m+3];
                }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);
            
        sums = xs[1];
        mbpss = (double)n1 * memMB / secs;
        printf("%7d", (int)mbpss);

        n1 = nn;
        do
        {
            for (m=0; m<ki; m++)
            {
                  xi[m] = zeroi;
                  yi[m] = onei;
            }          
                    
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<ki; m=m+inc)
                {
                     xi[m] = xi[m] + yi[m];
                     xi[m+1] = xi[m+1] + yi[m+1];
                     xi[m+2] = xi[m+2] + yi[m+2]; 
                     xi[m+3] = xi[m+3] + yi[m+3]; 
                }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);
     
        sumi = xi[1];
        mbpsi = (double)n1 * memMB / secs;
        fprintf(outfile, "%7d%7d%7d", (int)mbpsd, (int)mbpss, (int)mbpsi);
        printf("%7d", (int)mbpsi);
        
        memMB = (double)kd * 8 / 1000000;
    
        n1 = nn;
        do
        {              
            for (m=0; m<kd+inc; m++)
            {
                  xd[m] = zerod;
                  yd[m] = m;
            }        
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<kd; m=m+inc)
                {
                   xd[m] = yd[m];
                   xd[m+1]=yd[m+1];
                   xd[m+2]=yd[m+2];
                   xd[m+3]=yd[m+3];
                }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);
            
        sumd = xd[kd-1]+1;
        mbpsd = (double)n1 * memMB / secs;
        printf("%7d", (int)mbpsd);
        
        n1 = nn;
        do
        {
            for (m=0; m<ks+inc; m++)
            {
                  xs[m] = zeros;
                  ys[m] = m;
            }        
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<ks; m=m+inc)
                {
                   xs[m] = ys[m];
                   xs[m+1]=ys[m+1];
                   xs[m+2]=ys[m+2];
                   xs[m+3]=ys[m+3];
                }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);
        
        sums = xs[ks-1]+1;    
        mbpss = (double)n1 * memMB / secs;
        printf("%7d", (int)mbpss);

        n1 = nn;
        do
        {
            for (m=0; m<ki+inc; m++)
            {
                  xi[m] = zeroi;
                  yi[m] = m;
            }
                    
            start_time();
            for (i=0; i<n1; i++)
            {
// #pragma omp parallel for
                for (m=0; m<ki; m=m+inc)
                {
                   xi[m] = yi[m];
                   xi[m+1]=yi[m+1];
                   xi[m+2]=yi[m+2];
                   xi[m+3]=yi[m+3];
                }
            }
            end_time();
            checkTime();
        }
        while (secs < runSecs);

        sumi = xi[ki-1]+1;
        mbpsi = (double)n1 * memMB / secs;
        printf("%7d\n", (int)mbpsi);

        fprintf(outfile, "%7d%7d%7d\n", (int)mbpsd, (int)mbpss, (int)mbpsi);
        fflush(outfile);   
    }
    local_time();
    printf("\n                End of test %s\n", timeday);
    fprintf(outfile, "\n                End of test %s", timeday);        

    fprintf (outfile, "\nSYSTEM INFORMATION\n\nFrom File /proc/cpuinfo\n");
    fprintf (outfile, "%s \n", configdata[0]);
    fprintf (outfile, "\nFrom File /proc/version\n");
    fprintf (outfile, "%s \n", configdata[1]);
    fprintf (outfile, "\n");
    fflush(outfile);

     free(yd);
     free(xd);

    char moredata[1024];
    printf("Type additional information to include in memSpeed.txt - Press Enter\n");
    if (fgets (moredata, sizeof(moredata), stdin) != NULL)
             fprintf (outfile, "Additional information - %s\n", moredata);

    fflush(outfile);
    fclose(outfile);
    return 1;
}




 

