meta-perl
=========
This layer provides commonly-used perl related recipes such as perl libraries
in the Comprehensive Perl Archive Network.

Contents and Help
-----------------

In this section the contents of the layer is listed, along with a short
help for each package.

         -- libdbi-perl --
         The DBI is a database access module for the Perl programming language.
         It defines a set of methods, variables, and conventions that provide
         a consistent database interface, independent of the actual database
         being used.
                      |<- Scope of DBI ->|
                           .-.   .--------------.   .-------------.
           .-------.       | |---| XYZ Driver   |---| XYZ Engine  |
           | Perl  |       | |   `--------------'   `-------------'
           | script|  |A|  |D|   .--------------.   .-------------.
           | using |--|P|--|B|---|Oracle Driver |---|Oracle Engine|
           | DBI   |  |I|  |I|   `--------------'   `-------------'
           | API   |       | |...
           |methods|       | |... Other drivers
           `-------'       | |...
                           `-'

        -- libdbd-sqlite-perl --
        DBD::SQLite is a Perl DBI driver for SQLite, that includes the entire
        thing in the distribution. So in order to get a fast transaction capable
        RDBMS working for your perl project you simply have to install this
        module, and nothing else.

        usage: there is a test case to show you how it works

        1) vim local.conf:
        ...
        IMAGE_INSTALL:append = " libdbd-sqlite-perl"
        PERL_DBM_TEST = "1"
        ...
        2) build core-image-sato and boot the target

        3) run "sqlite-perl-test.pl" on target. This script includes five
           operations create/insert/update/delete/select to do with a table.

        More information can be found in the recipe's git log.

Dependencies
------------

This layer depends on:

  URI: git://git.openembedded.org/openembedded-core
  branch: nanbield 

Adding the meta-perl layer to your build
---------------------------------------

In order to use this layer, you need to make the build system aware of
it.

Assuming the meta-perl layer exists at the top-level of your
yocto build tree, you can add it to the build system by adding the
location of the meta-perl layer to bblayers.conf, along with any
other layers needed. e.g.:

  BBLAYERS ?= " \
    /path/to/oe-core/meta \
    /path/to/layer/meta-perl \

Maintenance
-----------

Send patches / pull requests to openembedded-devel@lists.openembedded.org with
'[meta-perl][nanbield]' in the subject.

When sending single patches, please using something like:
git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix='meta-perl][nanbield][PATCH'

Layer maintainers: Armin Kuster <akuster808@gmail.com>
    

License
-------

All metadata is MIT licensed unless otherwise stated. Source code included
in tree for individual recipes is under the LICENSE stated in each recipe
(.bb file) unless otherwise stated.
