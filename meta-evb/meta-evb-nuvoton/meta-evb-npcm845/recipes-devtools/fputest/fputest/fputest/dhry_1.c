/*
 * Compile on Raspberry Pi for 64 bit
 *
 *  gcc-6 dhry_1.c dhry_2.c cpuidc.c -lm -lrt -O3 -march=armv8-a -o dhrystonePi64
 *
 * #define options   "armv8 64 Bit"
 *************************************************************************
 *
 *                   "DHRYSTONE" Benchmark Program
 *                   -----------------------------
 *
 *  Version:    C, Version 2.1
 *
 *  File:       dhry_1.c (part 2 of 3)
 *
 *  Date:       May 25, 1988
 *
 *  Author:     Reinhold P. Weicker
 *
 *************************************************************************
 *
 */

 #include <time.h>
 #include <stdlib.h>
 #include <stdio.h>
 #include "dhry.h"
 #include "cpuidh.h"

 #ifdef CNNT
    #define options   "Non-optimised"
    #define opt "0"
 #else
//    #define options   "Optimised"
//    #define options   "Opt 3 32 Bit"
    #define options   "armv8 64 Bit"
    #define opt ""
 #endif
 

/* Global Variables: */
 
Rec_Pointer     Ptr_Glob,
                 Next_Ptr_Glob;
int             Int_Glob;
 Boolean         Bool_Glob;
 char            Ch_1_Glob,
                 Ch_2_Glob;
 int             Arr_1_Glob [50];
 int             Arr_2_Glob [50] [50];
 
 char Reg_Define[40] = "Register option      Selected.";
 

 Enumeration Func_1 (Capital_Letter Ch_1_Par_Val,
                                           Capital_Letter Ch_2_Par_Val);
   /* 
   forward declaration necessary since Enumeration may not simply be int
   */
 
 #ifndef ROPT
 #define REG
         /* REG becomes defined as empty */
         /* i.e. no register variables   */
 #else
 #define REG register
 #endif

 void Proc_1 (REG Rec_Pointer Ptr_Val_Par);
 void Proc_2 (One_Fifty *Int_Par_Ref);
 void Proc_3 (Rec_Pointer *Ptr_Ref_Par);
 void Proc_4 (); 
 void Proc_5 ();
 void Proc_6 (Enumeration Enum_Val_Par, Enumeration *Enum_Ref_Par);
 void Proc_7 (One_Fifty Int_1_Par_Val, One_Fifty Int_2_Par_Val,
                                              One_Fifty *Int_Par_Ref);
 void Proc_8 (Arr_1_Dim Arr_1_Par_Ref, Arr_2_Dim Arr_2_Par_Ref,
                               int Int_1_Par_Val, int Int_2_Par_Val);
                               
 Boolean Func_2 (Str_30 Str_1_Par_Ref, Str_30 Str_2_Par_Ref);

 
 /* variables for time measurement: */
 
 #define Too_Small_Time 2
                 /* Measurements should last at least 2 seconds */
 
 double         User_Time;
 
 double          Microseconds,
                 Dhrystones_Per_Second,
                 Vax_Mips;
 
 /* end of variables for time measurement */
 
 
 void main (int argc, char *argv[])
 /*****/
 
   /* main program, corresponds to procedures        */
   /* Main and Proc_0 in the Ada version             */
 {
 
         One_Fifty   Int_1_Loc;
   REG   One_Fifty   Int_2_Loc;
         One_Fifty   Int_3_Loc;
   REG   char        Ch_Index;
         Enumeration Enum_Loc;
         Str_30      Str_1_Loc;
         Str_30      Str_2_Loc;
   REG   int         Run_Index;
   REG   int         Number_Of_Runs; 
         int         endit, count = 10;
         FILE        *Ap;
         int         errors = 0;
         int         i;
         int         nopause = 1;

 
   /* Initializations */
    if (argc > 1)
     {
        switch (argv[1][0])
         {
             case 'N':
                nopause = 0;
                break;
             case 'n':
               nopause = 0;
               break;
         }
      }
 
   if ((Ap = fopen("Dhry.txt","a+")) == NULL)
     {
        printf(" Can not open Dhry.txt\n\n");
        printf(" Press Enter\n\n");
        int g = getchar();
        exit(1);
     }

/***********************************************************************
 *         Change for compiler and optimisation used                   *
 ***********************************************************************/
 
   Next_Ptr_Glob = (Rec_Pointer) malloc (sizeof (Rec_Type));
   Ptr_Glob = (Rec_Pointer) malloc (sizeof (Rec_Type));
 
   Ptr_Glob->Ptr_Comp                    = Next_Ptr_Glob;
   Ptr_Glob->Discr                       = Ident_1;
   Ptr_Glob->variant.var_1.Enum_Comp     = Ident_3;
   Ptr_Glob->variant.var_1.Int_Comp      = 40;
   strcpy (Ptr_Glob->variant.var_1.Str_Comp, 
           "DHRYSTONE PROGRAM, SOME STRING");       
   strcpy (Str_1_Loc, "DHRYSTONE PROGRAM, 1'ST STRING");
 
   Arr_2_Glob [8][7] = 10;
         /* Was missing in published program. Without this statement,   */
         /* Arr_2_Glob [8][7] would have an undefined value.            */
         /* Warning: With 16-Bit processors and Number_Of_Runs > 32000, */
         /* overflow may occur for this array element.                  */
 
    getDetails();


    printf("##########################################\n"); 

   printf ("\n");
   printf ("Dhrystone Benchmark, Version 2.1 (Language: C or C++)\n");
   printf ("\n");
   
   printf ("Optimisation    %s\n", options);
   #ifdef ROPT
       printf ("Register option selected\n\n");
   #else
       printf ("Register option not selected\n\n");
       strcpy(Reg_Define, "Register option  Not selected.");
   #endif


   Number_Of_Runs = 5000; //5000

   do
   {

       Number_Of_Runs = Number_Of_Runs * 2;
       count = count - 1;
       Arr_2_Glob [8][7] = 10;
        
       /***************/
       /* Start timer */
       /***************/
  
       start_time();
   
       for (Run_Index = 1; Run_Index <= Number_Of_Runs; ++Run_Index)
       {
 
         Proc_5();
         Proc_4();
           /* Ch_1_Glob == 'A', Ch_2_Glob == 'B', Bool_Glob == true */
         Int_1_Loc = 2;
         Int_2_Loc = 3;
         strcpy (Str_2_Loc, "DHRYSTONE PROGRAM, 2'ND STRING");
         Enum_Loc = Ident_2;
         Bool_Glob = ! Func_2 (Str_1_Loc, Str_2_Loc);
           /* Bool_Glob == 1 */
         while (Int_1_Loc < Int_2_Loc)  /* loop body executed once */
         {
           Int_3_Loc = 5 * Int_1_Loc - Int_2_Loc;
             /* Int_3_Loc == 7 */
           Proc_7 (Int_1_Loc, Int_2_Loc, &Int_3_Loc);
             /* Int_3_Loc == 7 */
           Int_1_Loc += 1;
         }   /* while */
            /* Int_1_Loc == 3, Int_2_Loc == 3, Int_3_Loc == 7 */
         Proc_8 (Arr_1_Glob, Arr_2_Glob, Int_1_Loc, Int_3_Loc);
           /* Int_Glob == 5 */
         Proc_1 (Ptr_Glob);
         for (Ch_Index = 'A'; Ch_Index <= Ch_2_Glob; ++Ch_Index)
                              /* loop body executed twice */
         {
           if (Enum_Loc == Func_1 (Ch_Index, 'C'))
               /* then, not executed */
             {
               Proc_6 (Ident_1, &Enum_Loc);
               strcpy (Str_2_Loc, "DHRYSTONE PROGRAM, 3'RD STRING");
               Int_2_Loc = Run_Index;
               Int_Glob = Run_Index;
             }
         }
           /* Int_1_Loc == 3, Int_2_Loc == 3, Int_3_Loc == 7 */
         Int_2_Loc = Int_2_Loc * Int_1_Loc;
         Int_1_Loc = Int_2_Loc / Int_3_Loc;
         Int_2_Loc = 7 * (Int_2_Loc - Int_3_Loc) - Int_1_Loc;
           /* Int_1_Loc == 1, Int_2_Loc == 13, Int_3_Loc == 7 */
         Proc_2 (&Int_1_Loc);
           /* Int_1_Loc == 5 */
 
       }   /* loop "for Run_Index" */
 
       /**************/
       /* Stop timer */
       /**************/
 
       end_time();
       User_Time = secs;
             
       printf ("%12.0f runs %6.2f seconds \n",(double) Number_Of_Runs, User_Time);
       if (User_Time > 2)
       {
             count = 0;
       }
       else
       {
             if (User_Time < 0.05)
             {
                  Number_Of_Runs = Number_Of_Runs * 5;
             }
       }
   }   /* calibrate/run do while */
   while (count >0);
 
   printf ("\n");
   printf ("Final values (* implementation-dependent):\n");
   printf ("\n");
   printf ("Int_Glob:      ");
   if (Int_Glob == 5)  printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d  ", Int_Glob);
      
   printf ("Bool_Glob:     ");
   if (Bool_Glob == 1) printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d\n", Bool_Glob);
      
   printf ("Ch_1_Glob:     ");
   if (Ch_1_Glob == 'A')  printf ("O.K.  ");               
   else                   printf ("WRONG ");
   printf ("%c  ", Ch_1_Glob);
         
   printf ("Ch_2_Glob:     ");
   if (Ch_2_Glob == 'B')  printf ("O.K.  ");
   else                   printf ("WRONG ");
   printf ("%c\n",  Ch_2_Glob);
   
   printf ("Arr_1_Glob[8]: ");
   if (Arr_1_Glob[8] == 7)  printf ("O.K.  ");
   else                     printf ("WRONG ");
   printf ("%d  ", Arr_1_Glob[8]);
            
   printf ("Arr_2_Glob8/7: ");
   if (Arr_2_Glob[8][7] == Number_Of_Runs + 10)
                          printf ("O.K.  ");
   else                   printf ("WRONG ");
   printf ("%10d\n", Arr_2_Glob[8][7]);
   
   printf ("Ptr_Glob->            ");
   printf ("  Ptr_Comp:       *    %d\n", (int) Ptr_Glob->Ptr_Comp);
   
   printf ("  Discr:       ");
   if (Ptr_Glob->Discr == 0)  printf ("O.K.  ");
   else                       printf ("WRONG ");
   printf ("%d  ", Ptr_Glob->Discr);
            
   printf ("Enum_Comp:     ");
   if (Ptr_Glob->variant.var_1.Enum_Comp == 2)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d\n", Ptr_Glob->variant.var_1.Enum_Comp);
      
   printf ("  Int_Comp:    ");
   if (Ptr_Glob->variant.var_1.Int_Comp == 17)  printf ("O.K.  ");
   else                                         printf ("WRONG ");
   printf ("%d ", Ptr_Glob->variant.var_1.Int_Comp);
      
   printf ("Str_Comp:      ");
   if (strcmp(Ptr_Glob->variant.var_1.Str_Comp,
                        "DHRYSTONE PROGRAM, SOME STRING") == 0)
                        printf ("O.K.  ");
   else                printf ("WRONG ");   
   printf ("%s\n", Ptr_Glob->variant.var_1.Str_Comp);
   
   printf ("Next_Ptr_Glob->       "); 
   printf ("  Ptr_Comp:       *    %d", (int) Next_Ptr_Glob->Ptr_Comp);
   printf (" same as above\n");
   
   printf ("  Discr:       ");
   if (Next_Ptr_Glob->Discr == 0)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d  ", Next_Ptr_Glob->Discr);
   
   printf ("Enum_Comp:     ");
   if (Next_Ptr_Glob->variant.var_1.Enum_Comp == 1)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d\n", Next_Ptr_Glob->variant.var_1.Enum_Comp);
   
   printf ("  Int_Comp:    ");
   if (Next_Ptr_Glob->variant.var_1.Int_Comp == 18)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d ", Next_Ptr_Glob->variant.var_1.Int_Comp);
   
   printf ("Str_Comp:      ");
   if (strcmp(Next_Ptr_Glob->variant.var_1.Str_Comp,
                        "DHRYSTONE PROGRAM, SOME STRING") == 0)
                        printf ("O.K.  ");
   else                printf ("WRONG ");   
   printf ("%s\n", Next_Ptr_Glob->variant.var_1.Str_Comp);
   
   printf ("Int_1_Loc:     ");
   if (Int_1_Loc == 5)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d  ", Int_1_Loc);
      
   printf ("Int_2_Loc:     ");
   if (Int_2_Loc == 13)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d\n", Int_2_Loc);
   
   printf ("Int_3_Loc:     ");
   if (Int_3_Loc == 7)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d  ", Int_3_Loc);
   
   printf ("Enum_Loc:      ");
   if (Enum_Loc == 1)
                        printf ("O.K.  ");
   else                printf ("WRONG ");
   printf ("%d  \n", Enum_Loc);
   
   
   printf ("Str_1_Loc:                             ");
   if (strcmp(Str_1_Loc, "DHRYSTONE PROGRAM, 1'ST STRING") == 0)
                        printf ("O.K.  ");

   else                printf ("WRONG ");   
   printf ("%s\n", Str_1_Loc);

   printf ("Str_2_Loc:                             ");
   if (strcmp(Str_2_Loc, "DHRYSTONE PROGRAM, 2'ND STRING") == 0)
                        printf ("O.K.  ");
   else                printf ("WRONG ");   
   printf ("%s\n", Str_2_Loc);
         

   printf ("\n");
    
 
   if (User_Time < Too_Small_Time)
   {
     printf ("Measured time too small to obtain meaningful results\n");
     printf ("Please increase number of runs\n");
     printf ("\n");
   }
   else
   {
     printf ("\nFrom File /proc/cpuinfo\n");
     printf("%s\n", configdata[0]);
     printf ("\nFrom File /proc/version\n");
     printf("%s\n\n", configdata[1]);

     Microseconds = User_Time * Mic_secs_Per_Second 
                         / (double) Number_Of_Runs;
     Dhrystones_Per_Second = (double) Number_Of_Runs / User_Time;
     Vax_Mips = Dhrystones_Per_Second / 1757.0;
 
     printf ("Nanoseconds one Dhrystone run: %12.2lf\n", Microseconds * 1000);
     printf ("Dhrystones per Second:         %12.0lf\n", Dhrystones_Per_Second);
     printf ("VAX  MIPS rating =             %12.2lf\n",Vax_Mips);
     printf ("\n");


/************************************************************************
 *                Add results to output file Dhry.txt                   *
 ************************************************************************/
   local_time();
   fprintf (Ap, " #####################################################\n\n");                     
   fprintf (Ap, " Dhrystone Benchmark 2.1 %s, %s\n", options, timeday);
   fprintf (Ap, " Nanoseconds one Dhrystone run: %12.2lf\n", Microseconds * 1000);
   fprintf (Ap, " Dhrystones per Second:         %12.0lf\n", Dhrystones_Per_Second);
   fprintf (Ap, " VAX MIPS rating =              %12.2lf\n\n",Vax_Mips);

  

   if (Int_Glob != 5)
   {
       fprintf (Ap, " Wrong result Int_Glob Was %d Should be 5\n", Int_Glob);
       errors = errors + 1;
   }
   if (Bool_Glob != 1)
   {
       fprintf (Ap, " Wrong result Bool_Glob Was %d Should be 1\n", Bool_Glob);
       errors = errors + 1;
   }
   if (Ch_1_Glob != 'A')
   {
       fprintf (Ap, " Wrong result Ch_1_Glob Was %c Should be A\n", Ch_1_Glob);
       errors = errors + 1;
   }
   if (Ch_2_Glob != 'B')
   {
       fprintf (Ap, " Wrong result Ch_2_Glob Was %c Should be B\n", Ch_2_Glob);
       errors = errors + 1;
   }         
   if (Arr_1_Glob[8] != 7)
   {
       fprintf (Ap, " Wrong result Arr_1_Glob[8] Was %d Should be 7\n", Arr_1_Glob[8]);
       errors = errors + 1;
   }
   if (Arr_2_Glob[8][7] != Number_Of_Runs + 10)
   {
       fprintf (Ap, " Wrong result Arr_2_Glob[8][7] Was %d Should be %d\n", Arr_2_Glob[8][7], Number_Of_Runs + 10);
       errors = errors + 1;
   }
   if (Ptr_Glob->Discr != 0)
   {
       fprintf (Ap, " Wrong result Ptr_Glob->Discr Was %d Should be 0\n", Ptr_Glob->Discr);
       errors = errors + 1;
   }
   if (Ptr_Glob->variant.var_1.Enum_Comp != 2)
   {
       fprintf (Ap, " Wrong result Ptr_Glob->variant.var_1.Enum_Comp Was %d Should be 2\n", Ptr_Glob->variant.var_1.Enum_Comp);
       errors = errors + 1;
   }
   if (Ptr_Glob->variant.var_1.Int_Comp != 17)
   {
       fprintf (Ap, " Wrong result Ptr_Glob->variant.var_1.Int_Comp Was %d Should be 17\n", Ptr_Glob->variant.var_1.Int_Comp);
       errors = errors + 1;
   }
   if (strcmp(Ptr_Glob->variant.var_1.Str_Comp,  "DHRYSTONE PROGRAM, SOME STRING") != 0)
   {
       fprintf (Ap, " Wrong result Ptr_Glob->variant.var_1.Str_Comp Was %s Should be DHRYSTONE PROGRAM, SOME STRING\n", Ptr_Glob->variant.var_1.Str_Comp);
       errors = errors + 1;
   }
   if (Next_Ptr_Glob->Discr != 0)
   {
       fprintf (Ap, " Wrong result Next_Ptr_Glob->Discr Was %d Should be 0\n", Next_Ptr_Glob->Discr);
       errors = errors + 1;
   }
   if (Next_Ptr_Glob->variant.var_1.Enum_Comp != 1)
   {
       fprintf (Ap, " Wrong result Next_Ptr_Glob->variant.var_1.Enum_Comp Was %d Should be 1\n", Next_Ptr_Glob->variant.var_1.Enum_Comp);
       errors = errors + 1;
   }
   if (Next_Ptr_Glob->variant.var_1.Int_Comp != 18)
   {
       fprintf (Ap, " Wrong result Next_Ptr_Glob->variant.var_1.Int_Comp Was %d Should be 18\n", Next_Ptr_Glob->variant.var_1.Int_Comp);
       errors = errors + 1;
   }
   if (strcmp(Next_Ptr_Glob->variant.var_1.Str_Comp,  "DHRYSTONE PROGRAM, SOME STRING") != 0)
   {
       fprintf (Ap, " Wrong result Next_Ptr_Glob->variant.var_1.Str_Comp Was %s Should be DHRYSTONE PROGRAM, SOME STRING\n", Next_Ptr_Glob->variant.var_1.Str_Comp);
       errors = errors + 1;
   }
   if (Int_1_Loc != 5)
   {
       fprintf (Ap, " Wrong result Int_1_Loc Was %d Should be 5\n", Int_1_Loc);
       errors = errors + 1;
   }
   if (Int_2_Loc != 13)
   {
       fprintf (Ap, " Wrong result Int_2_Loc Was %d Should be 13\n", Int_2_Loc);
       errors = errors + 1;
   }
   if (Int_3_Loc != 7)
   {
       fprintf (Ap, " Wrong result Int_3_Loc Was %d Should be 7\n", Int_3_Loc);
       errors = errors + 1;
   }
   if (Enum_Loc != 1)
   {
       fprintf (Ap, " Wrong result Enum_Loc Was %d Should be 1\n", Enum_Loc);
       errors = errors + 1;
   }
   if (strcmp(Str_1_Loc, "DHRYSTONE PROGRAM, 1'ST STRING") != 0)
   {
       fprintf (Ap, " Wrong result Str_1_Loc Was %s Should be DHRYSTONE PROGRAM, 1'ST STRING\n", Str_1_Loc);
       errors = errors + 1;
   }
   if (strcmp(Str_2_Loc, "DHRYSTONE PROGRAM, 2'ND STRING") != 0)
   {
       fprintf (Ap, " Wrong result Str_2_Loc Was %s Should be DHRYSTONE PROGRAM, 2'ND STRING\n", Str_2_Loc);
       errors = errors + 1;
   }
   if (errors == 0)
   {
       fprintf (Ap, " Numeric results were correct\n\n");
   }
   else
   {
       fprintf (Ap, "\n");
   }
   fprintf (Ap, " #####################################################\n\n"); 
   fprintf (Ap, "SYSTEM INFORMATION\n\nFrom File /proc/cpuinfo\n");
   fprintf (Ap, "%s \n", configdata[0]);
   fprintf (Ap, "\nFrom File /proc/version\n");
   fprintf (Ap, "%s \n", configdata[1]);
   fprintf (Ap, "\n");
   fflush(Ap);
                 
   }

    if (nopause)
    {
      char moredata[1024];
      printf("Type additional information to include in Dhry.txt - Press Enter\n");
      if (fgets (moredata, sizeof(moredata), stdin) != NULL)
             fprintf (Ap, "Additional information - %s\n", moredata);
    }
    fclose(Ap);
 }
 
 void Proc_1 (REG Rec_Pointer Ptr_Val_Par)
 /******************/
 
     /* executed once */
 {
   REG Rec_Pointer Next_Record = Ptr_Val_Par->Ptr_Comp;  
                                         /* == Ptr_Glob_Next */
   /* Local variable, initialized with Ptr_Val_Par->Ptr_Comp,    */
   /* corresponds to "rename" in Ada, "with" in Pascal           */
   
   structassign (*Ptr_Val_Par->Ptr_Comp, *Ptr_Glob);
   Ptr_Val_Par->variant.var_1.Int_Comp = 5;
   Next_Record->variant.var_1.Int_Comp 
         = Ptr_Val_Par->variant.var_1.Int_Comp;
   Next_Record->Ptr_Comp = Ptr_Val_Par->Ptr_Comp;
   Proc_3 (&Next_Record->Ptr_Comp);
     /* Ptr_Val_Par->Ptr_Comp->Ptr_Comp 
                         == Ptr_Glob->Ptr_Comp */
   if (Next_Record->Discr == Ident_1)
     /* then, executed */
   {
     Next_Record->variant.var_1.Int_Comp = 6;
     Proc_6 (Ptr_Val_Par->variant.var_1.Enum_Comp, 
            &Next_Record->variant.var_1.Enum_Comp);
     Next_Record->Ptr_Comp = Ptr_Glob->Ptr_Comp;
     Proc_7 (Next_Record->variant.var_1.Int_Comp, 10, 
            &Next_Record->variant.var_1.Int_Comp);
   }
   else /* not executed */
     structassign (*Ptr_Val_Par, *Ptr_Val_Par->Ptr_Comp);
 } /* Proc_1 */
 
 
 void Proc_2 (One_Fifty *Int_Par_Ref)
 /******************/
     /* executed once */
     /* *Int_Par_Ref == 1, becomes 4 */
 
 {
   One_Fifty  Int_Loc;
   Enumeration   Enum_Loc;
 
   Int_Loc = *Int_Par_Ref + 10;
   do /* executed once */
     if (Ch_1_Glob == 'A')
       /* then, executed */
     {
       Int_Loc -= 1;
       *Int_Par_Ref = Int_Loc - Int_Glob;
       Enum_Loc = Ident_1;
     } /* if */
   while (Enum_Loc != Ident_1); /* true */
 } /* Proc_2 */
 
 
 void Proc_3 (Rec_Pointer *Ptr_Ref_Par)
 /******************/
     /* executed once */
     /* Ptr_Ref_Par becomes Ptr_Glob */
 
 {
   if (Ptr_Glob != Null)
     /* then, executed */
     *Ptr_Ref_Par = Ptr_Glob->Ptr_Comp;
   Proc_7 (10, Int_Glob, &Ptr_Glob->variant.var_1.Int_Comp);
 } /* Proc_3 */
 
 
void Proc_4 () /* without parameters */
 /*******/
     /* executed once */
 {
   Boolean Bool_Loc;
 
   Bool_Loc = Ch_1_Glob == 'A';
   Bool_Glob = Bool_Loc | Bool_Glob;
   Ch_2_Glob = 'B';
 } /* Proc_4 */
 
 
 void Proc_5 () /* without parameters */
 /*******/
     /* executed once */
 {
   Ch_1_Glob = 'A';
   Bool_Glob = false;
 } /* Proc_5 */
 
 
         /* Procedure for the assignment of structures,          */
         /* if the C compiler doesn't support this feature       */
 #ifdef  NOSTRUCTASSIGN
 memcpy (d, s, l)
 register char   *d;
 register char   *s;
 register int    l;
 {
         while (l--) *d++ = *s++;
 }
 #endif



