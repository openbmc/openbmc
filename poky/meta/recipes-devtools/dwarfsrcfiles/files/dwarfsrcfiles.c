// dwarfsrcfiles.c - Get source files associated with the dwarf in a elf file.
// gcc -Wall -g -O2 -lelf -ldw -o dwarfsrcfiles dwarfsrcfiles.c 
//
// Copyright (C) 2011, Mark Wielaard <mjw@redhat.com>
//
// This file is free software.  You can redistribute it and/or modify
// it under the terms of the GNU General Public License (GPL); either
// version 2, or (at your option) any later version.

#include <argp.h>
#include <stdio.h>
#include <stdlib.h>

#include <dwarf.h>
#include <elfutils/libdw.h>
#include <elfutils/libdwfl.h>

static int
process_cu (Dwarf_Die *cu_die)
{
  Dwarf_Attribute attr;
  const char *name;
  const char *dir = NULL;
  
  Dwarf_Files *files;
  size_t n;
  int i;
  
  if (dwarf_tag (cu_die) != DW_TAG_compile_unit)
    {
      fprintf (stderr, "DIE isn't a compile unit");
      return -1;
    }
  
  if (dwarf_attr (cu_die, DW_AT_name, &attr) == NULL)
    {
      fprintf(stderr, "CU doesn't have a DW_AT_name");
      return -1;
    }
  
  name = dwarf_formstring (&attr);
  if (name == NULL)
    {
      fprintf(stderr, "Couldn't get DW_AT_name as string, %s",
	     dwarf_errmsg (-1));
      return -1;
    }
  
  if (dwarf_attr (cu_die, DW_AT_comp_dir, &attr) != NULL)
    {
      dir = dwarf_formstring (&attr);
      if (dir == NULL)
	{
	  fprintf(stderr, "Couldn't get DW_AT_comp_die as string, %s",
		 dwarf_errmsg (-1));
	  return -1;
	}
    }
  
  if (dir == NULL)
    printf ("%s\n", name);
  else
    printf ("%s/%s\n", dir, name);
  
  if (dwarf_getsrcfiles (cu_die, &files, &n) != 0)
    {
      fprintf(stderr, "Couldn't get CU file table, %s",
	     dwarf_errmsg (-1));
      return -1;
    }
  
  for (i = 1; i < n; i++)
    {
      const char *file = dwarf_filesrc (files, i, NULL, NULL);
      if (dir != NULL && file[0] != '/')
	printf ("\t%s/%s\n", dir, file);
      else
	printf ("\t%s\n", file);
    }
  
  return 0;
}

int
main (int argc, char **argv)
{
  char* args[5];
  int res = 0;
  Dwfl *dwfl;
  Dwarf_Addr bias;
  
  if (argc != 2) {
    fprintf(stderr, "Usage %s <file>", argv[0]);
    exit(EXIT_FAILURE);
  }
  
  // Pretend "dwarfsrcfiles -e <file>" was given, so we can use standard
  // dwfl argp parser to open the file for us and get our Dwfl. Useful
  // in case argument is an ET_REL file (like kernel modules). libdwfl
  // will fix up relocations for us.
  args[0] = argv[0];
  args[1] = "-e";
  args[2] = argv[1];
  // We don't want to follow debug linked files due to the way OE processes
  // files, could race against changes in the linked binary (e.g. objcopy on it)
  args[3] = "--debuginfo-path";
  args[4] = "/not/exist";
  
  argp_parse (dwfl_standard_argp (), 5, args, 0, NULL, &dwfl);
  
  Dwarf_Die *cu = NULL;
  while ((cu = dwfl_nextcu (dwfl, cu, &bias)) != NULL)
    res |= process_cu (cu);
  
  dwfl_end (dwfl);

  return res;
}
