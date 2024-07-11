# Standards for contributing to Yocto Project documentation

This document attemps to standardize the way the Yocto Project
documentation is created.

It is currently a work in progress.

## Automatic style validation

There is an ongoing effort to automate style validation
through the [Vale](https://vale.sh/). To try it, run:

    $ make stylecheck

Note that this just applies to text. Therefore, the syntax
conventions described below still apply.

If you wish to add a new word to an "accept.txt" file
(./styles/config/vocabularies/<Vocab>/accept.txt),
make sure the spelling and capitalization matches
what Wikipedia or the project defining this word uses.

## Text standards

### Bulleted lists

Though Sphinx supports both the ``*`` and ``-`` characters
for introducing bulleted lists, we have chosen to use
only ``-`` for this purpose.

Though not strictly required by Sphinx, we have also chosen
to use two space characters after ``-`` to introduce each
list item:

    -  Paragraph 1

    -  Paragraph 2

As shown in the above example, there should also be an empty
line between each list item.

An exception to this rule is when the list items are just made
of a few words, instead of entire paragraphs:

    -  Item 1
    -  Item 2

This is again a matter of style, not syntax.

### Line wrapping

Source code for the documentation shouldn't have lines
wider than 80 characters. This makes patch lines more
readable and code easier to quote in e-mail clients.

If you have to include long commands or lines in configuration
files, provided the syntax makes this possible, split them
into multiple lines, using the ``\`` character.

Here is an example:

    $ scripts/install-buildtools \
      --without-extended-buildtools \
      --base-url https://downloads.yoctoproject.org/releases/yocto \
      --release yocto-4.0.1 \
      --installer-version 4.0.1

Exceptions are granted for file contents whose lines
cannot be split without infringing syntactic rules
or reducing readability, as well as for command output
which should be kept unmodified.

### File, tool and command names

File, tool, command and package names should be double tick-quoted.
For example, ``` ``conf/local.conf`` ``` is preferred over
`"conf/local.conf"`.

### Project names

Project names should be introduced with single quotes, to have them rendered
with an italic font and make them easier to distinguish from command names
(double tick-quoted) and from regular English words.

An exception is when project names appear in hyperlinks, as nested markup
is not supported by Sphinx yet.

Project names should also be capitalized (or not) in the same way they are on
Wikipedia, or on their own project pages if they are not described on
Wikipedia. If a project name isn't capitalized, it should remain so even
at the beginning of a sentence.

For example:

* ``` `BitBake` ```
* ``` `ftrace` ```

### Variables

Every variable should be mentioned with:

    :term:`VARIABLE`

This assumes that `VARIABLE` is described either
in the Yocto Project documentation variable index (`ref-manual/variables.rst`)
or in the BitBake User Manual
(`doc/bitbake-user-manual/bitbake-user-manual-ref-variables.rst`)

If it is not described yet, the variable should be added to the
glossary before or in the same patch it is used, so that `:term:` can be used.

## ReStructured Text Syntax standards

This section has not been filled yet

## Adding screenshots

The preferred format for adding screenshots is
[Portable Network Graphics (PNG)](https://en.wikipedia.org/wiki/Portable_Network_Graphics).
Compared to the JPEG format, PNG is lossless and compresses screenshots better.

Screenshots are stored in a `figures/` subdirectory.

To include a screenshot in PNG format:

    .. image:: figures/user-configuration.png
       :align: center

A diagram with many details usually needs to use
the whole page width to be readable on all media.
In this case, the `:align:` directive is unnecessary:

       :scale: 100%

Conversely, you may also shrink some images to
to prevent them from filling the whole page width:

       :scale: 50%

For some types of screenshots, for example showing
programs displaying photographs or playing video, the JPEG
format may be more appropriate, because it is better at
compressing real-life images:

    .. image:: figures/vlc.jpg
       :align: center
       :scale: 75%

## Adding new diagrams

New diagrams should be added in
[Scalable Vector Graphics (SVG) format](https://en.wikipedia.org/wiki/Scalable_Vector_Graphics).
Thanks to this, diagrams render nicely at any zoom level on generated documentation,
instead of being pixelated.

The recommended tool for creating SVG diagrams for the Yocto Project
documentation is [Inkscape](https://inkscape.org/).

### Colors

The "GNOME HIG Colors" palette is used in our diagrams
(get it from <https://gitlab.gnome.org/Teams/Design/HIG-app-icons/-/blob/master/GNOME%20HIG.gpl>
if you don't get it from Inkscape).

It's easier to use than the default one in Inkscape,
as it has fewer but sufficient colors. This is a way
to guarantee that we use consistent colors across the
diagrams used in our documentation.

See <https://inkscape-manuals.readthedocs.io/en/latest/palette.html>
for details about working with color palettes in Inkscape.

### Template diagram

The `template/` directory contains a `template.svg` file
to make it easier to create diagrams.
In particular, you can use it to copy standard text, shapes,
arrows and clipart to the new SVG document.

### Fonts

The recommended font for description text and labels is `Nimbus Roman`, size 10.
Labels are in bold.

`template.svg` contains ready-to-copy labels.

### Text boxes

Text boxes are rectangle boxes, with rounded corners, and contain centered text
or labels. Their stroke color is slightly darker than their fill color.

See `template.svg` for example boxes with different colors, which are ready
to be reused.

### Clipart

Only [Public Domain](https://en.wikipedia.org/wiki/Public_domain)
files are accepted for clipart. [Openclipart](https://openclipart.org)
is the best known and recommended source of such clipart.

It is also required to state the source of each new clipart in the commit log,
to make it possible to check its origin.

For the sake of consistency across diagrams, we recommend
to use existing cliparts, whenever possible.

If cliparts in `template.svg` do not satisfy your requirements, you can
add to said file new cliparts, from e.g. Openclipart. Newly added
cliparts shall stay consistent in style and color with existing ones.
