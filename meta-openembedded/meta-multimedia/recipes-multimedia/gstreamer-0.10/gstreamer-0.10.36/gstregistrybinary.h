/* GStreamer
 * Copyright (C) 1999,2000 Erik Walthinsen <omega@cse.ogi.edu>
 *                    2000 Wim Taymans <wim.taymans@chello.be>
 *
 * gstregistry.h: Header for registry handling
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/* SUGGESTIONS AND TODO :
** ====================
** - Use a compressed registry, but would induce performance loss
** - Encrypt the registry, for security purpose, but would also reduce performances
** - Also have a non-mmap based cache reading (work with file descriptors)
*/

#ifndef __GST_REGISTRYBINARY_H__
#define __GST_REGISTRYBINARY_H__

#ifdef HAVE_CONFIG_H
#  include "config.h"
#endif

#include <stdio.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/mman.h>
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif

#include <gst/gst_private.h>
#include <gst/gstelement.h>
#include <gst/gsttypefind.h>
#include <gst/gsttypefindfactory.h>
#include <gst/gsturi.h>
#include <gst/gstinfo.h>
#include <gst/gstenumtypes.h>
#include <gst/gstregistry.h>
#include <gst/gstpadtemplate.h>

#include "glib-compat-private.h"
#include <glib/gstdio.h>

/* A magic, written at the beginning of the file */
#define GST_MAGIC_BINARY_REGISTRY_STR "\xc0\xde\xf0\x0d"
#define GST_MAGIC_BINARY_REGISTRY_LEN (4)
#define GST_MAGIC_BINARY_VERSION_LEN (64)

typedef struct _GstBinaryRegistryMagic
{
  char magic[GST_MAGIC_BINARY_REGISTRY_LEN];
  char version[GST_MAGIC_BINARY_VERSION_LEN];
} GstBinaryRegistryMagic;


/* Used to store pointers to write */
typedef struct _GstBinaryChunck
{
  void *data;
  unsigned int size;
} GstBinaryChunck;


/* A structure containing (staticely) every information needed for a plugin
**
** Notes :
** "nfeatures" is used to say how many GstBinaryPluginFeature structures we will have 
** right after the structure itself.
*/

/* Various lenght defines for our GstBinaryPluginElement structure 
** Note : We could eventually use smaller size
*/
#define GST_BINARY_REGISTRY_NAME_LEN (256)
#define GST_BINARY_REGISTRY_DESCRIPTION_LEN (1024)
#define GST_BINARY_REGISTRY_VERSION_LEN (64)
#define GST_BINARY_REGISTRY_LICENSE_LEN (256)
#define GST_BINARY_REGISTRY_SOURCE_LEN (256)
#define GST_BINARY_REGISTRY_PACKAGE_LEN (1024)
#define GST_BINARY_REGISTRY_ORIGIN_LEN (1024)

typedef struct _GstBinaryPluginElement
{
  char name[GST_BINARY_REGISTRY_NAME_LEN];
  char description[GST_BINARY_REGISTRY_DESCRIPTION_LEN];
  char filename[_POSIX_PATH_MAX];
  char version[GST_BINARY_REGISTRY_VERSION_LEN];
  char license[GST_BINARY_REGISTRY_LICENSE_LEN];
  char source[GST_BINARY_REGISTRY_SOURCE_LEN];
  char package[GST_BINARY_REGISTRY_PACKAGE_LEN];
  char origin[GST_BINARY_REGISTRY_ORIGIN_LEN];
  unsigned long size;
  unsigned long m32p;
  unsigned int nfeatures;
} GstBinaryPluginElement;


/* A structure containing the plugin features
**
** Note :
** "npadtemplates" is used to store the number of GstBinaryPadTemplate structures following the structure itself.
** "ninterfaces" is used to store the number of GstBinaryInterface structures following the structure itself.
** "nuritypes" is used to store the number of GstBinaryUriType structures following the structure itself.
*/
#define GST_BINARY_REGISTRY_TYPENAME_TYPENAME_LEN (256)
#define GST_BINARY_REGISTRY_TYPENAME_NAME_LEN (256)
#define GST_BINARY_REGISTRY_TYPENAME_LONGNAME_LEN (1024)
#define GST_BINARY_REGISTRY_TYPENAME_CLASS_LEN (512)
#define GST_BINARY_REGISTRY_TYPENAME_DESCRIPTION_LEN (1024)
#define GST_BINARY_REGISTRY_TYPENAME_AUTHOR_LEN (256)

typedef struct _GstBinaryPluginFeature
{
  char typename[GST_BINARY_REGISTRY_TYPENAME_TYPENAME_LEN];
  char name[GST_BINARY_REGISTRY_TYPENAME_NAME_LEN];
  unsigned long rank;
  char longname[GST_BINARY_REGISTRY_TYPENAME_LONGNAME_LEN];
  char class[GST_BINARY_REGISTRY_TYPENAME_CLASS_LEN];
  char description[GST_BINARY_REGISTRY_TYPENAME_DESCRIPTION_LEN];
  char author[GST_BINARY_REGISTRY_TYPENAME_AUTHOR_LEN];
  unsigned int npadtemplates;
  unsigned int ninterfaces;
  unsigned int nuritypes;
} GstBinaryPluginFeature;


/* 
** A structure containing the static pad templates of a plugin feature 
*/
#define GST_BINARY_REGISTRY_PADTEMPLATE_NAME_LEN (256)
#define GST_BINARY_REGISTRY_PADTEMPLATE_CAP_LEN (1024)

typedef struct _GstBinaryPadTemplate
{
  char name[GST_BINARY_REGISTRY_PADTEMPLATE_NAME_LEN];
  char cap[GST_BINARY_REGISTRY_PADTEMPLATE_CAP_LEN];
  int direction;					/* Either 0:"sink" or 1:"src" */
  GstPadPresence presence;
} GstBinaryPadTemplate;

/*
** A very simple structure defining the plugin feature interface string
*/
#define GST_BINARY_REGISTRY_INTERFACE_INTERFACE_LEN (512)
typedef struct _GstBinaryInterface
{
  char interface[GST_BINARY_REGISTRY_INTERFACE_INTERFACE_LEN];
  unsigned long size;
} GstBinaryInterface;

/* Uri Type */
typedef struct _GstBinaryUriType
{
  GstURIType type;
  unsigned long nuriprotocols;
} GstBinaryUriType;

/* 
** Function prototypes
*/

/* Local prototypes */
inline static gboolean gst_registry_binary_write(GstRegistry *registry, const void *mem, const ssize_t size);
inline static gboolean gst_registry_binary_initialize_magic(GstBinaryRegistryMagic *m);
static gboolean gst_registry_binary_fill_feature(GList **list, GstPluginFeature *, GstBinaryPluginFeature *, const char *);
static gboolean gst_registry_binary_save_plugin(GList **list, GstRegistry *registry, GstPlugin *plugin);
static gchar *gst_registry_binary_check_magic(gchar *in);
static GstPluginFeature *gst_registry_binary_load_feature(GstBinaryPluginFeature *);
static unsigned long gst_registry_binary_get_binary_plugin(GstRegistry *registry, gchar *in);

/* Exportable */
gboolean gst_registry_binary_write_cache(GstRegistry *registry, const char *location);
gboolean gst_registry_binary_read_cache(GstRegistry *registry, const char *location);

#endif /* !__GST_REGISTRYBINARY_H__ */


