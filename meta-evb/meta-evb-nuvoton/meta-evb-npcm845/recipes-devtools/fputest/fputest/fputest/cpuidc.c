/*
;  nasm -f elf cpuida.asm   for cpuida.o
;  gcc cpuidc.c -c          for cpuidc.o
;  gcc test.c cpuidc.o cpuida.o -lrt -lc -lm -o test
;  ./test
*/

  #include <stdio.h>
  #include "cpuidh.h"
  #include <stdlib.h>     
  #include <string.h> 
  #include <time.h>
  #include <math.h>
  #include <sys/time.h>
  #include <sys/resource.h>

  char    configdata[2][1024];
  char    timeday[30];
  char    idString1[100] = " ";
  char    idString2[100] = " ";
  double  theseSecs = 0.0;
  double  startSecs = 0.0;
  double  secs;
  struct  timespec tp1;
  // struct  timeval tv;
  

  double  ramGB;

  FILE * info;

  int CPUconf;
  int CPUavail;
 

  unsigned int millisecs  = 0;
  

  #include <sys/sysinfo.h>
  #include <sys/utsname.h> 


  void local_time()
  {
     time_t t;

     t = time(NULL);
     sprintf(timeday, "%s", asctime(localtime(&t)));
     return;
  }

  void getSecs()
  {
    clock_gettime(CLOCK_REALTIME, &tp1);
//    clock_gettime(CLOCK_THREAD_CPUTIME_ID, &tp1);

     theseSecs =  (tp1.tv_sec) + (tp1.tv_nsec) / 1e9;               
/* 
     gettimeofday(&tv, NULL);
     theseSecs = (double)(tv.tv_sec) + (double)(tv.tv_usec) / 1000000.0;  
     time_t sec;
     sec = time(NULL);
     theseSecs = (double)sec;
*/
     return;
  }

  void start_time()
  {
      getSecs();
      startSecs = theseSecs;
      return;
  }

  void end_time()
  {
      getSecs();
      secs = theseSecs - startSecs;
      millisecs = (int)(1000.0 * secs);
      return;
  }    

  int getDetails()
  {
     size_t bytes;

     info = fopen ("/proc/cpuinfo", "r");
     if (info == NULL)
     {
          sprintf (configdata[0], " Cannot open /proc/cpuinfo\n");
          return 1;
     }
     bytes = fread (configdata[0], 1, sizeof(configdata[0]), info);
     fclose (info);
     if (bytes == 0) sprintf (configdata[0], " No data read from /proc/cpuinfo\n");
      
     info = fopen ("/proc/version", "r");
     if (info == NULL)
     {
          sprintf (configdata[0], " Cannot open /proc/version\n");
          return 1;
     }
     bytes = fread (configdata[1], 1, sizeof(configdata[1]), info);
     fclose (info);
     if (bytes == 0) sprintf (configdata[1], " No data read from /proc/version\n");
     return 0; 
  }

