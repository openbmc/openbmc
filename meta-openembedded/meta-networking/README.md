meta-networking
===============

This layer is intended to be a central point for networking-related
packages and configuration.  It should be useful directly on top of
oe-core and compliments meta-openembedded.  It should be primarily useful
to the following groups:

      - Anyone building a small networking device (eg. a home router /
        bridge / switch).

      - Anyone wanting to add network services to their device (eg.
        anything that might benefit from a small ftp/tftp server)

Dependencies
------------

This layer depends on:

URI: git://git.openembedded.org/openembedded-core
branch: nanbield

For some recipes, the meta-oe layer is required:

URI: git://git.openembedded.org/meta-openembedded
subdirectory: meta-oe
branch: nanbield

URI: git://git.openembedded.org/meta-openembedded
subdirectory: meta-python
branch: nanbield

Maintenance
-----------
Layer maintainers: Armin Kuster <akuster808@gmail.com>


Please see the MAINTAINERS file for information on contacting the
maintainers of this layer, as well as instructions for submitting patches. 
