<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<META http-equiv="content-language" CONTENT="ru-RU">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Registration</title>
<!--    Bootstap START-->

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1"
	crossorigin="anonymous">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/js/bootstrap.bundle.min.js"
	integrity="sha384-ygbV9kiqUc6oa4msXn9868pTtWMgiQaeYH7/t7LECLbyPA2x65Kgf80OJFdroafW"
	crossorigin="anonymous"></script>
<!--    Bootstrap END -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.3.0/font/bootstrap-icons.css">
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
	integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
	crossorigin="anonymous"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
	integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
	crossorigin="anonymous"></script>
<script
	src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"
	integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T"
	crossorigin="anonymous"></script>

<script src='https://kit.fontawesome.com/a076d05399.js'></script>


<link rel="stylesheet" href="main.css">
</head>
<body class="otherPageBody">

	<c:import url="/WEB-INF/jspf/header.jspf" />

	<main>
		<form class="form" action="Registration" method="post">
			<div class="row row-cols-auto">
				<div class="col-md-3">
					<label for="inputFirstName" class="form-label">Имя<span
						style="color: red">*</span></label> <input type="text"
						class="form-control" id="inputFirstName" name="firstName" required>

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputFirstName').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

				<div class="col-md-3">
					<label for="inputLastName" class="form-label">Фамилия<span
						style="color: red">*</span></label> <input type="text"
						class="form-control" id="inputLastName" name="lastName" required>

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputLastName').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>

			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputEmail" class="form-label">Электронная
						почта<span style="color: red">*</span>
					</label> <input type="text" class="form-control" id="inputEmail"
						name="email" required>

				</div>
				<button type="button" style="color: black" class="btn btn-link">
					<i class="far fa-times-circle"></i>
				</button>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputEmail').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

			</div>

			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputPhone" class="form-label">Контактный номер
						телефон<span style="color: red">*</span>
					</label> <input type="tel" class="form-control" id="inputPhone"
						name="phoneNumber" required>

				</div>
				<button type="button" style="color: black" class="btn btn-link">
					<i class="far fa-times-circle"></i>
				</button>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputPhone').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

			</div>

			<div class="row row-cols-auto">
				<div class="col-md-3">
					<label for="inputPassword" class="form-label">Пароль<span
						style="color: red">*</span></label> <input type="password"
						class="form-control" id="inputPassword" name="password" required>

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputPassword').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

				<div class="col-md-3">
					<label for="inputConfirm" class="form-label">Повторите
						пароль<span style="color: red">*</span>
					</label> <input type="password" class="form-control" id="inputConfirm"
						name="confirmPassword" required>

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputConfirm').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>

			<div class="row row-cols-auto">
				<div class="col-md-3">
					<label for="inputStreet" class="form-label">Улица/Проспект/Переулок</label>
					<input type="text" class="form-control" id="inputStreet"
						name="street">

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputStreet').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

				<div class="col-md-3">
					<label for="inputHouse" class="form-label">Номер дома</label> <input
						type="text" class="form-control" id="inputHouse" name="house">

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputHouse').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>
			<div class="row row-cols-auto">
				<div class="col-md-3">
					<label for="inputApartment" class="form-label">Номер
						квартиры</label> <input type="text" class="form-control"
						id="inputApartment" name="apartment">

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputApartment').value = ''">
					<i class="far fa-times-circle"></i>
				</button>


				<div class="col-md-3">
					<label for="inputPorch" class="form-label">Номер подъезда</label> <input
						type="text" class="form-control" id="inputPorch" name="porch">

				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputPorch').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>
			<button type="submit" class="btn btn-light">Зарегистрироваться</button>
		</form>
	</main>

	<c:import url="/WEB-INF/jspf/footer.jspf" />


</body>
</html>