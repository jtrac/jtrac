require 'watir'
include Watir
require 'test/unit'

class TestJtrac < Test::Unit::TestCase

	def test_01_login
		$ie = IE.new
		$ie.goto 'http://localhost:8080/jtrac'
		$ie.text_field(:name, 'j_username').set('admin')
		$ie.text_field(:name, 'j_password').set('admin')
		$ie.button(:value, 'Submit').click
		assert($ie.contains_text('DASHBOARD'))
	end
	
	def test_02_create_user
		$ie.link(:text, 'OPTIONS').click
		$ie.link(:text, 'Users').click
		$ie.link(:text, 'Create New User').click
		$ie.text_field(:name, 'user.loginName').set('testuser')
		$ie.text_field(:name, 'user.name').set('Test User')
		$ie.text_field(:name, 'user.email').set('foo@bar.com')
		$ie.button(:value, 'Submit').click
		$ie.button(:value, 'Cancel').click
		assert($ie.contains_text('Test User'))
	end

end
