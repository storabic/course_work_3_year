# Generated by Django 3.2.6 on 2021-09-19 15:56

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('coursework', '0007_auto_20210912_1917'),
    ]

    operations = [
        migrations.AddField(
            model_name='pool',
            name='sum',
            field=models.FloatField(default=0),
        ),
    ]