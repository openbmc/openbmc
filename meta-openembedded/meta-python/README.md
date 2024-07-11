meta-python
================================

Introduction
-------------------------

This layer is intended to be the home of python modules for OpenEmbedded.

Dependencies
-------------------------

The meta-python layer depends on:

	URI: git://git.openembedded.org/openembedded-core
	layers: meta
	branch: scarthgap

	URI: git://git.openembedded.org/meta-openembedded
	layers: meta-oe
	branch: scarthgap

Contributing
-------------------------

The meta-openembedded mailinglist
(openembedded-devel@lists.openembedded.org) is used for questions,
comments and patch review. It is subscriber only, so please register
before posting.

Send pull requests to openembedded-devel@lists.openembedded.org with
'[meta-python][scarthgap]' in the subject.

When sending single patches, please use something like:
git send-email -M -1 --to=openembedded-devel@lists.openembedded.org --subject-prefix='meta-python][scarthgap][PATCH'

Maintenance
-------------------------

Layer maintainers: Armin Kuster <akuster808@gmail.com>
