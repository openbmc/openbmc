Django Aggregate If: Condition aggregates for Django
====================================================

.. image:: https://travis-ci.org/henriquebastos/django-aggregate-if.png?branch=master
    :target: https://travis-ci.org/henriquebastos/django-aggregate-if
    :alt: Test Status

.. image:: https://landscape.io/github/henriquebastos/django-aggregate-if/master/landscape.png
    :target: https://landscape.io/github/henriquebastos/django-aggregate-if/master
    :alt: Code Helth

.. image:: https://pypip.in/v/django-aggregate-if/badge.png
    :target: https://crate.io/packages/django-aggregate-if/
    :alt: Latest PyPI version

.. image:: https://pypip.in/d/django-aggregate-if/badge.png
    :target: https://crate.io/packages/django-aggregate-if/
    :alt: Number of PyPI downloads

*Aggregate-if* adds conditional aggregates to Django.

Conditional aggregates can help you reduce the ammount of queries to obtain
aggregated information, like statistics for example.

Imagine you have a model ``Offer`` like this one:

.. code-block:: python

    class Offer(models.Model):
        sponsor = models.ForeignKey(User)
        price = models.DecimalField(max_digits=9, decimal_places=2)
        status = models.CharField(max_length=30)
        expire_at = models.DateField(null=True, blank=True)
        created_at = models.DateTimeField(auto_now_add=True)
        updated_at = models.DateTimeField(auto_now=True)

        OPEN = "OPEN"
        REVOKED = "REVOKED"
        PAID = "PAID"

Let's say you want to know:

#. How many offers exists in total;
#. How many of them are OPEN, REVOKED or PAID;
#. How much money was offered in total;
#. How much money is in OPEN, REVOKED and PAID offers;

To get these informations, you could query:

.. code-block:: python

    from django.db.models import Count, Sum

    Offer.objects.count()
    Offer.objects.filter(status=Offer.OPEN).aggregate(Count('pk'))
    Offer.objects.filter(status=Offer.REVOKED).aggregate(Count('pk'))
    Offer.objects.filter(status=Offer.PAID).aggregate(Count('pk'))
    Offer.objects.aggregate(Sum('price'))
    Offer.objects.filter(status=Offer.OPEN).aggregate(Sum('price'))
    Offer.objects.filter(status=Offer.REVOKED).aggregate(Sum('price'))
    Offer.objects.filter(status=Offer.PAID).aggregate(Sum('price'))

In this case, **8 queries** were needed to retrieve the desired information.

With conditional aggregates you can get it all with only **1 query**:

.. code-block:: python

    from django.db.models import Q
    from aggregate_if import Count, Sum

    Offer.objects.aggregate(
        pk__count=Count('pk'),
        pk__open__count=Count('pk', only=Q(status=Offer.OPEN)),
        pk__revoked__count=Count('pk', only=Q(status=Offer.REVOKED)),
        pk__paid__count=Count('pk', only=Q(status=Offer.PAID)),
        pk__sum=Sum('price'),
        pk__open__sum=Sum('price', only=Q(status=Offer.OPEN)),
        pk__revoked__sum=Sum('price'), only=Q(status=Offer.REVOKED)),
        pk__paid__sum=Sum('price'), only=Q(status=Offer.PAID))
    )

Installation
------------

*Aggregate-if* works with Django 1.4, 1.5, 1.6 and 1.7.

To install it, simply:

.. code-block:: bash

    $ pip install django-aggregate-if

Inspiration
-----------

There is a 5 years old `ticket 11305`_ that will (*hopefully*) implement this feature into
Django 1.8.

Using Django 1.6, I still wanted to avoid creating custom queries for very simple
conditional aggregations. So I've cherry picked those ideas and others from the
internet and built this library.

This library uses the same API and tests proposed on `ticket 11305`_, so when the
new feature is available you can easily replace ``django-aggregate-if``.

Limitations
-----------

Conditions involving joins with aliases are not supported yet. If you want to
help adding this feature, you're welcome to check the `first issue`_.

Contributors
------------

* `Henrique Bastos <http://github.com/henriquebastos>`_
* `Iuri de Silvio <https://github.com/iurisilvio>`_
* `Hampus Stjernhav <https://github.com/champ>`_
* `Bradley Martsberger <https://github.com/martsberger>`_
* `Markus Bertheau <https://github.com/mbertheau>`_
* `end0 <https://github.com/end0>`_
* `Scott Sexton <https://github.com/scottsexton>`_
* `Mauler <https://github.com/mauler>`_
* `trbs <https://github.com/trbs>`_

Changelog
---------

0.5
    - Support for Django 1.7

0.4
    - Use tox to run tests.
    - Add support for Django 1.6.
    - Add support for Python3.
    - The ``only`` parameter now freely supports joins independent of the main query.
    - Adds support for alias relabeling permitting excludes and updates with aggregates filtered on remote foreign key relations.

0.3.1
    - Fix quotation escaping.
    - Fix boolean casts on Postgres.

0.2
    - Fix postgres issue with LIKE conditions.

0.1
    - Initial release.


License
=======

The MIT License.

.. _ticket 11305: https://code.djangoproject.com/ticket/11305
.. _first issue: https://github.com/henriquebastos/django-aggregate-if/issues/1
