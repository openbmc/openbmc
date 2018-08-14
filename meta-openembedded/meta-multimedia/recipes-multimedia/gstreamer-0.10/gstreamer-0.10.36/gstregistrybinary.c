/* GStreamer
 * Copyright (C) 1999,2000 Erik Walthinsen <omega@cse.ogi.edu>
 *                    2000 Wim Taymans <wtay@chello.be>
 *                    2005 David A. Schleef <ds@schleef.org>
 *
 * gstregistryxml.c: GstRegistry object, support routines
 *
 * This library is free software; you can redistribute it and/or
 * modify it ulnder the terms of the GNU Library General Public
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


#include <gst/gstregistrybinary.h>

/*
** Simple handy function to write a memory location to the registry cache file
*/
inline static gboolean
gst_registry_binary_write(GstRegistry *registry, const void *mem, const ssize_t size)
{
  if (write(registry->cache_file, mem, size) != size)
    {
      GST_ERROR("Failed to write binary registry element: ptr=%p size=%u error=%s\n",
		mem, size, strerror(errno));
      return FALSE;
    }
  return TRUE;
}

/*
** Save features GstBinary style
*/
static gboolean 
gst_registry_binary_fill_feature(GList **list, GstPluginFeature *orig, GstBinaryPluginFeature *dest, const char *name)
{
  GstBinaryChunck *chk;

  if ((chk = calloc(1, sizeof (GstBinaryChunck))) == NULL)
    return FALSE;

  chk->data = dest;
  chk->size = sizeof (GstBinaryPluginFeature);

  *list = g_list_append(*list, chk);

  dest->rank = orig->rank;
  if (!strncpy(dest->typename, name, GST_BINARY_REGISTRY_TYPENAME_TYPENAME_LEN) ||
      !strncpy(dest->name, orig->name, GST_BINARY_REGISTRY_TYPENAME_NAME_LEN))
    {
      GST_ERROR("Failed to write binary registry feature");
      goto fail;
    }
  
  if (GST_IS_ELEMENT_FACTORY(orig))
    {
      GstElementFactory *factory = GST_ELEMENT_FACTORY(orig);
      
      if (!strncpy(dest->longname, factory->details.longname, GST_BINARY_REGISTRY_TYPENAME_LONGNAME_LEN) ||
	  !strncpy(dest->class, factory->details.klass, GST_BINARY_REGISTRY_TYPENAME_CLASS_LEN) ||
	  !strncpy(dest->description, factory->details.description, GST_BINARY_REGISTRY_TYPENAME_DESCRIPTION_LEN) ||
	  !strncpy(dest->author, factory->details.author, GST_BINARY_REGISTRY_TYPENAME_AUTHOR_LEN))
	{
	  GST_ERROR("Failed to write binary registry feature");
	  goto fail;
	}
    }
  
  dest->npadtemplates = dest->ninterfaces = dest->nuritypes = 0;
  return TRUE;

 fail:
  free(chk);
  return FALSE;
}


/*
** Initialize the GstBinaryRegistryMagic, setting both our magic number and gstreamer major/minor version
*/
inline static gboolean
gst_registry_binary_initialize_magic(GstBinaryRegistryMagic *m)
{
  if (!strncpy(m->magic, GST_MAGIC_BINARY_REGISTRY_STR, GST_MAGIC_BINARY_REGISTRY_LEN) ||
      !strncpy(m->version, GST_MAJORMINOR, GST_BINARY_REGISTRY_VERSION_LEN))
    {
      GST_ERROR("Failed to write magic to the registry magic structure");
      return FALSE;
    }
  return TRUE;
}

/*
** Check GstBinaryRegistryMagic validity.
** Return a pointer pointing right after the magic structure
*/
static gchar *
gst_registry_binary_check_magic(gchar *in)
{
  GstBinaryRegistryMagic *m = (GstBinaryRegistryMagic *) in;

  if (m == NULL || m->magic == NULL || m->version == NULL)
    {
      GST_ERROR("Binary registry magic structure is broken");
      return NULL;
    }
  if (strncmp(m->magic, GST_MAGIC_BINARY_REGISTRY_STR, GST_MAGIC_BINARY_REGISTRY_LEN) != 0)
    {
      GST_ERROR("Binary registry magic is different : %02x%02x%02x%02x != %02x%02x%02x%02x",
		GST_MAGIC_BINARY_REGISTRY_STR[0] & 0xff, GST_MAGIC_BINARY_REGISTRY_STR[1] & 0xff,
		GST_MAGIC_BINARY_REGISTRY_STR[2] & 0xff, GST_MAGIC_BINARY_REGISTRY_STR[3] & 0xff,
		m->magic[0] & 0xff, m->magic[1] & 0xff, m->magic[2] & 0xff, m->magic[3] & 0xff);
      return NULL;
    }
  if (strncmp(m->version, GST_MAJORMINOR, GST_BINARY_REGISTRY_VERSION_LEN))
    {
      GST_ERROR("Binary registry magic version is different : %s != %s",
		GST_MAJORMINOR, m->version);
      return NULL;
    }
  return (in + sizeof (GstBinaryRegistryMagic));
}

/*
** Adapt a GstPlugin to our GstBinaryPluginElement structure, and write it to the 
** registry file.
*/   
static gboolean
gst_registry_binary_save_plugin(GList **list, GstRegistry *registry, GstPlugin *plugin)
{
  GstBinaryPluginElement *e;
  GstBinaryChunck *chk;
  GList *walk;

  if ((e = calloc(1, sizeof (GstBinaryPluginElement))) == NULL ||
      (chk = calloc(1, sizeof (GstBinaryChunck))) == NULL)
    return FALSE;

  chk->data = e;
  chk->size = sizeof (GstBinaryPluginElement);
  *list = g_list_append(*list, chk);

  if (!strncpy(e->name, plugin->desc.name, GST_BINARY_REGISTRY_NAME_LEN)		       	||
      !strncpy(e->description, plugin->desc.description, GST_BINARY_REGISTRY_DESCRIPTION_LEN)	||
      !strncpy(e->filename, plugin->filename, _POSIX_PATH_MAX)					||
      !strncpy(e->version, plugin->desc.version, GST_BINARY_REGISTRY_VERSION_LEN)		||
      !strncpy(e->license, plugin->desc.license, GST_BINARY_REGISTRY_LICENSE_LEN)		||
      !strncpy(e->source, plugin->desc.source, GST_BINARY_REGISTRY_SOURCE_LEN)			||
      !strncpy(e->package, plugin->desc.package, GST_BINARY_REGISTRY_PACKAGE_LEN)		||
      !strncpy(e->origin, plugin->desc.origin, GST_BINARY_REGISTRY_ORIGIN_LEN))
    {
      GST_DEBUG("Can't adapt GstPlugin to GstBinaryPluginElement");
      goto fail;
    }

  e->size = plugin->file_size;
  e->m32p = plugin->file_mtime;
  
  GList *ft_list = gst_registry_get_feature_list_by_plugin(registry, plugin->desc.name);

  for (walk = ft_list; walk; walk = g_list_next(walk), e->nfeatures++)
    {
      GstPluginFeature *curfeat = GST_PLUGIN_FEATURE (walk->data);
      GstBinaryPluginFeature *newfeat;
      const char *feat_name = g_type_name(G_OBJECT_TYPE(curfeat));
      
      if ((newfeat = calloc(1, sizeof (GstBinaryPluginFeature))) == NULL)
	  goto fail;

      if (!feat_name || !gst_registry_binary_fill_feature(list, curfeat, newfeat, feat_name))
	{
	  GST_ERROR("Can't fill plugin feature, aborting.");
	  goto fail;
	}
    }

  GST_DEBUG("Found %d features in plugin \"%s\"\n", e->nfeatures, e->name);
  return TRUE;

 fail:
  free(chk);
  free(e);
  return FALSE;
}

/*
** Write the cache to file. Part of the code was taken from gstregistryxml.c
*/
gboolean 
gst_registry_binary_write_cache(GstRegistry *registry, const char *location)
{
  GList *walk;
  char *tmp_location;
  GstBinaryRegistryMagic *magic;
  GstBinaryChunck *magic_chunck;
  GList *to_write = NULL;
 
  GST_INFO("Writing binary registry cache");

  g_return_val_if_fail (GST_IS_REGISTRY (registry), FALSE);
  tmp_location = g_strconcat (location, ".tmpXXXXXX", NULL);
  registry->cache_file = g_mkstemp (tmp_location);
  if (registry->cache_file == -1)
    {
      char *dir;

      /* oops, I bet the directory doesn't exist */
      dir = g_path_get_dirname (location);
      g_mkdir_with_parents (dir, 0777);
      g_free (dir);
      
      registry->cache_file = g_mkstemp (tmp_location);
    }

  if (registry->cache_file == -1)
    goto fail;

  if ((magic = calloc(1, sizeof (GstBinaryRegistryMagic))) == NULL ||
      !gst_registry_binary_initialize_magic(magic))
    goto fail;

  if ((magic_chunck = calloc(1, sizeof (GstBinaryChunck))) == NULL)
    goto fail;

  magic_chunck->data = magic;
  magic_chunck->size = sizeof (GstBinaryRegistryMagic);
  to_write = g_list_append(to_write, magic_chunck); 

  /* Iterate trough the list of plugins in the GstRegistry and adapt them to our structures */
  for (walk = g_list_last(registry->plugins); walk; walk = g_list_previous(walk))
    {
      GstPlugin *plugin = GST_PLUGIN(walk->data);
      
      if (!plugin->filename)
	continue;
	  
      if (plugin->flags & GST_PLUGIN_FLAG_CACHED)
	{
	  int ret;
	  struct stat statbuf;
	  
	  ret = g_stat (plugin->filename, &statbuf);
	  if ((ret = g_stat (plugin->filename, &statbuf)) < 0 	||
	      plugin->file_mtime != statbuf.st_mtime		||
	      plugin->file_size != statbuf.st_size)
	    continue;
	}

      if (!gst_registry_binary_save_plugin(&to_write, registry, plugin))
	{
	  GST_ERROR("Can't write binary plugin information for \"%s\"", plugin->filename);
	  continue; /* Try anyway */
	}
    }

  for (walk = g_list_first(to_write); walk; walk = g_list_next(walk))
    {
      GstBinaryChunck *cur = walk->data;

      if (!gst_registry_binary_write(registry, cur->data, cur->size))
	{
	  free(cur->data);
	  free(cur);
	  g_list_free(to_write);
	  goto fail;
	}
      free(cur->data);
      free(cur);
    }
  g_list_free(to_write);

  if (close(registry->cache_file) < 0)
    {
      GST_DEBUG("Can't close registry file : %s", strerror(errno));
      goto fail;
    }
  
  if (g_file_test (tmp_location, G_FILE_TEST_EXISTS)) {
#ifdef WIN32
    remove (location);
#endif
    rename (tmp_location, location);
  }

  g_free (tmp_location);
  return TRUE;

 fail:
  g_free(tmp_location);
  return FALSE;
}

static GstPluginFeature*
gst_registry_binary_load_feature(GstBinaryPluginFeature *in)
{
  GstPluginFeature *feature;
  GType type;

  if (!in->typename || !*(in->typename))
    return NULL;

  /*  GST_INFO("Plugin feature typename : %s", in->typename);*/

  if (!(type = g_type_from_name(in->typename)))
    {
      GST_ERROR("Unknown type from typename");
      return NULL;
    }
  feature = g_object_new (type, NULL);

  if (!feature) {
    GST_ERROR("Can't create feature from type");
    return NULL;
  }

  if (!GST_IS_PLUGIN_FEATURE (feature)) {
    /* don't really know what it is */
    if (GST_IS_OBJECT (feature))
      gst_object_unref (feature);
    else
      g_object_unref (feature);
    return NULL;
  }

  feature->name = g_strdup(in->name);
  feature->rank = in->rank;

  if (GST_IS_ELEMENT_FACTORY(feature))
    {
      GstElementFactory *factory = GST_ELEMENT_FACTORY(feature);

      factory->details.longname = g_strdup(in->longname);
      factory->details.klass = g_strdup(in->class);
      factory->details.description = g_strdup(in->description);
      factory->details.author = g_strdup(in->author);

      /*      GST_INFO("Element factory : %s", factory->details.longname); */
    }

  GST_DEBUG("Added feature %p with name %s", feature, feature->name);
  return feature;
}

/*
** Make a new plugin from current GstBinaryPluginElement structure
** and save it to the GstRegistry. Return an offset to the next
** GstBinaryPluginElement structure.
*/
static unsigned long
gst_registry_binary_get_binary_plugin(GstRegistry *registry, gchar *in)
{
  GstBinaryPluginElement *p = (GstBinaryPluginElement *) in;
  GstPlugin *plugin = NULL;
  GList *plugin_features = NULL;
  GstBinaryPluginFeature *feat;
  unsigned int i;
  unsigned long offset;

  plugin = g_object_new (GST_TYPE_PLUGIN, NULL);

  plugin->flags |= GST_PLUGIN_FLAG_CACHED;

  plugin->desc.name = g_strdup(p->name);
  plugin->desc.description= g_strdup(p->description);
  plugin->filename = g_strdup(p->filename);
  plugin->desc.version = g_strdup(p->version);
  plugin->desc.license = g_strdup(p->license);
  plugin->desc.source = g_strdup(p->source);
  plugin->desc.package = g_strdup(p->package);
  plugin->desc.origin = g_strdup(p->origin);
  plugin->file_mtime = p->m32p;
  plugin->file_size = p->size;
  plugin->basename = g_path_get_basename (plugin->filename);

  if (plugin->file_mtime < 0 || plugin->file_size < 0)
    {
      GST_ERROR("Plugin time or file size is not valid !");
      g_free(plugin);
      return -1;
    }

  if (p->nfeatures < 0)
    {
      GST_ERROR("The number of feature structure is not valid !");
      gst_object_unref(plugin);
      return -1;
    }

  for (feat = (GstBinaryPluginFeature *) (in + sizeof (GstBinaryPluginElement)), i = 0; 
       i < p->nfeatures; i++, feat++)
    {
      GstPluginFeature *gstfeat;

      if ((gstfeat = gst_registry_binary_load_feature(feat)) == NULL)
	{
	  g_list_free(plugin_features);
	  g_free(plugin);
	  GST_ERROR("Error while loading binary feature");
	  return -1;
	}
      gstfeat->plugin_name = g_strdup(plugin->desc.name);
      plugin_features = g_list_prepend(plugin_features, gstfeat);
    }
  
  GST_DEBUG("Added plugin \"%s\" to global registry from binary registry", plugin->desc.name); 
  GList *g;

  gst_registry_add_plugin (registry, plugin);
  for (g = plugin_features; g; g = g_list_next (g))
    gst_registry_add_feature (registry, GST_PLUGIN_FEATURE (g->data));
  /*  g_list_free(plugin_features); */

  offset = sizeof (GstBinaryPluginElement) + p->nfeatures * sizeof (GstBinaryPluginFeature);
  return offset;
}


/*
** Read the cache and adapt it to fill GstRegistry
*/ 
gboolean 
gst_registry_binary_read_cache(GstRegistry *registry, const char *location)
{
  GMappedFile *mapped = NULL;
  GTimer *timer = NULL;
  gchar *contents = NULL;
  gdouble seconds;
  unsigned long offset, inc;
  gsize size;

  /* make sure these types exist */
  GST_TYPE_ELEMENT_FACTORY;
  GST_TYPE_TYPE_FIND_FACTORY;
  GST_TYPE_INDEX_FACTORY;

  timer = g_timer_new ();

  if ((mapped = g_mapped_file_new(location, FALSE, NULL)) == NULL ||
      (contents = g_mapped_file_get_contents(mapped)) == NULL)
    {
      GST_ERROR("Can't load file : %s", strerror(errno));
      return FALSE;
    }
  if ((contents = gst_registry_binary_check_magic(contents)) == NULL)
    {
      GST_ERROR("Binary registry type not recognized (invalid magic)");
      g_mapped_file_free(mapped);
      return FALSE;
    }

  if ((size = g_mapped_file_get_length(mapped)) < sizeof (GstBinaryPluginElement))
    {
      GST_INFO("No binary plugins structure to read");
      return TRUE; /* This is not really an error */
    }

  for (offset = inc = 0; (offset + sizeof (GstBinaryPluginElement)) < size &&
	 (inc = gst_registry_binary_get_binary_plugin(registry, contents + offset)) > 0;
       offset += inc)
    ; /* May want in the future to do something here */
  if (inc < 0)
    {
      GST_DEBUG("Problem while reading binary registry");
      return FALSE;
    }

  g_timer_stop (timer);
  seconds = g_timer_elapsed (timer, NULL);
  g_timer_destroy (timer);

  GST_INFO ("loaded %s in %f seconds", location, seconds);

  if (mapped)
    g_mapped_file_free (mapped);
  return TRUE;
}
