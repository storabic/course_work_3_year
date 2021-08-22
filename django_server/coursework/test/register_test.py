import json

from django.test import TestCase
from coursework.models import User, Client
from coursework.views import register


class register_test(TestCase):
    def setUp(self):
        lion_user = User.objects.create_user(username='lion', password='lionpswd')
        lion_user.save()
        cat_user = User.objects.create_user(username='cat', password='catpswd')
        cat_user.save()

    def test_bad_body(self):
        req_body = {
            'login': 'cat',
        }
        response = self.client.post('/register', json.dumps(req_body), content_type='application/json')
        self.assertEqual(response.status_code, 400)
        self.assertEqual(b'{"reason": "pass json map with login and password in body"}', response.content)

    def test_non_unique_login(self):
        req_body = {
            'login': 'lion',
            'password': 'lionpswd1'
        }
        response = self.client.post('/register', json.dumps(req_body), content_type='application/json')
        self.assertEqual(response.status_code, 400)
        self.assertEqual(b'{"reason": "login is not unique"}', response.content)

    def test_bad_login(self):
        req_body = {
            'login': '#%@!dog≈≈å',
            'password': 'dogpswd1'
        }
        response = self.client.post('/register', json.dumps(req_body), content_type='application/json')
        self.assertEqual(response.status_code, 400)
        self.assertEqual(b'{"reason": "login is invalid"}', response.content)

    def test_success(self):
        req_body = {
            'login': 'dogger',
            'password': 'dogpswd1'
        }
        response = self.client.post('/register', json.dumps(req_body), content_type='application/json')
        self.assertEqual(response.status_code, 200)
        user_token = json.loads(response.content)['user_token']
        client = Client.objects.get(user_token=user_token)
        self.assertEqual(client.user.username, 'dogger')
