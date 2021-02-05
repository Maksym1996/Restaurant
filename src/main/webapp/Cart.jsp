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
<title>Your cart</title>
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

	<div align="center" style="font-size: 1em">
		Ваша корзина
		<p>
			Сумма заказа:
			<c:out value="${orderSumm}" />
			грн
		</p>
		<form class="form" action="Cart" method="post"
			style="margin-left: 2rem">
			<button type="submit" action="Cart" method="post"
				class="btn btn-info">Оформить заказ</button>

			<div class="row row-cols-auto" align="left">
				<div class="col-md-4">
					<label for="inputFirstName">Имя<span style="color: red">*</span></label>
					<input type="text" class="form-control" id="inputFirstName"
						name="firstName" required>
					<c:if
						test="${not empty errors and errors.containsKey('firstName')}">
						<p style="color: red">
							<c:out value="${errors.firstName}" />
						</p>
					</c:if>
				</div>
				<div class="col-md-4">
					<label for="inputLastName" class="formLabelCart">Фамилия<span
						style="color: red">*</span></label> <input type="text"
						class="form-control" id="inputLastName" name="lastName" required>
					<c:if test="${not empty errors and errors.containsKey('lastName')}">
						<p style="color: red">
							<c:out value="${errors.firstName}" />
						</p>
					</c:if>
				</div>
				<div class="col-md-4">
					<label for="inputPhoneNumber" class="formLabelCart">Номер
						телефона<span style="color: red">*</span>
					</label> <input type="tel" class="form-control" id="inputPhoneNumber"
						name="phoneNumber" required>
					<c:if
						test="${not empty errors and errors.containsKey('phoneNumber')}">
						<p style="color: red">
							<c:out value="${errors.phoneNumber}" />
						</p>
					</c:if>
					<c:if
						test="${not empty errors and errors.containsKey('phoneNumberPattern')}">
						<p style="color: red">
							<c:out value="${errors.phoneNumberPattern}" />
						</p>
					</c:if>
				</div>
				<div class="col-md-3">
					<label for="inputStreet" class="form-label">Улица<span
						style="color: red">*</span></label> <input type="text"
						class="form-control" id="inputStreet" name="street" required>
					<c:if test="${not empty errors and errors.containsKey('street')}">
						<p style="color: red">
							<c:out value="${errors.street}" />
						</p>
					</c:if>
				</div>
				<div class="col-md-3">
					<label for="inputHouse" class="form-label">Дом<span
						style="color: red">*</span></label> <input type="text"
						class="form-control" id="inputHouse" name="house" required>
					<c:if test="${not empty errors and errors.containsKey('house')}">
						<p style="color: red">
							<c:out value="${errors.house}" />
						</p>
					</c:if>
				</div>
				<div class="col-md-3">
					<label for="inputApartment" class="form-label">Квартира</label> <input
						type="text" class="form-control" id="inputApartment"
						name="apartment">
					<c:if
						test="${not empty errors and errors.containsKey('apartmentPattern')}">
						<p style="color: red">
							<c:out value="${errors.apartmentPattern}" />
						</p>
					</c:if>
				</div>
				<div class="col-md-3">
					<label for="inputPorch" class="form-label">Подъезд</label> <input
						type="text" class="form-control" id="inputPorch" name="porch">
					<c:if
						test="${not empty errors and errors.containsKey('porchPattern')}">
						<p style="color: red">
							<c:out value="${errors.porchPattern}" />
						</p>
					</c:if>
				</div>

			</div>
		</form>
	</div>
	<!-- Вывод контента -->
	<main class="container">
		<div class="row ">
			<c:forEach var="product" items="${productsList}">
				<div class="col-md-3" style="color: black">
					<div class="card cartBorder" style="width: 12rem">
						<img src=<c:out value = "${product.imageLink}"/>
							class="card-img-top" alt="Pizza" height="170" width="150">
						<div class="card-body">
							<h5 class="card-title">
								<c:out value="${product.name}" />
							</h5>

							<div class="row row-cols-auto">

								<font size=+2 class="col"> <c:out
										value="${product.price}" /> грн
								</font>

							</div>

						</div>
					</div>
				</div>
			</c:forEach>

		</div>


	</main>

	<c:import url="/WEB-INF/jspf/footer.jspf" />
</body>
</html>