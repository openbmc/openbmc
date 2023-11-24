meta-initramfs
==============

This layer contains the metadata necessary to build the klibc library and
utilities (shared and static) plus other tools useful for the creation of
small-sized initramfs.


Dependencies
------------

This layer depends on:

URI: git://git.openembedded.org/meta-openembedded
branch: nanbield


Maintenance
-----------

Send patches / pull requests to openembedded-devel@lists.openembedded.org
with '[meta-initramfs][nanbield]' in the subject.

When sending single patches, please using something like:
git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix='meta-initramfs][nanbield][PATCH'

Interm layer maintainer: Armin Kuster <akuster808@gmail.com>


License
-------

All metadata is MIT licensed unless otherwise stated. Source code included
in tree for individual recipes is under the LICENSE stated in each recipe
(.bb file) unless otherwise stated.
