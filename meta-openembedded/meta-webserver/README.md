meta-webserver
==============

This layer provides support for building web servers, web-based
applications and related software.



Dependencies
------------

This layer depends on:

URI: git://git.openembedded.org/openembedded-core
subdirectory: meta
branch: master

For some recipes, the meta-oe layer is required:

URI: git://git.openembedded.org/meta-openembedded
subdirectory: meta-oe
branch: master



Layout
------

recipes-httpd/      Web servers
recipes-php/        PHP applications
recipes-support/    Miscellaneous support recipes
recipes-webadmin/   Standalone web administration interfaces


Notes
-----

* This layer used to provide a modphp recipe that built mod_php, but
  this is now built as part of the php recipe in meta-oe. However, since
  apache2 is required to build mod_php, and apache2 recipe is in this
  layer and recipes in meta-oe can't depend on it, mod_php is not built
  by default. If you do wish to use mod_php, you need to add "apache2"
  to the PACKAGECONFIG value for the php recipe in order to enable it.
  See here for info on how to do that:

  http://www.yoctoproject.org/docs/current/ref-manual/ref-manual.html#var-PACKAGECONFIG


Maintenance
-----------

Send patches / pull requests to openembedded-devel@lists.openembedded.org
with '[meta-webserver]' in the subject.

Layer maintainer: Derek Straka <derek@asterius.io>


License
-------

All metadata is MIT licensed unless otherwise stated. Source code included
in tree for individual recipes is under the LICENSE stated in each recipe
(.bb file) unless otherwise stated.

This README document is Copyright (C) 2012 Intel Corporation.

