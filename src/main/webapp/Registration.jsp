<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt"%>
<f:setLocale value="${lang}" />
<f:setBundle basename="Bundles" />
<!DOCTYPE html>
<html>
<head>
<META http-equiv="content-language" CONTENT="ru-RU">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><f:message key="registration" /></title>
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

	<c:import url="/WEB-INF/resources/header.jsp" />

	<main>
		<!-- Registration form -->
		<form class="form" action="Registration" method="post"
			style="margin-left: 3rem">
			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputFirstName" class="form-label"><f:message
							key="firstName" /><span style="color: red">*</span></label> <input
						type="text" class="form-control" id="inputFirstName"
						name="firstName" required>
					<c:if
						test="${not empty errors and errors.containsKey('firstName')}">
						<p style="color: red">
							<c:out value="${errors.firstName}" />
						</p>
					</c:if>

				</div>

				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputFirstName').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>
			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputLastName" class="form-label"><f:message
							key="lastName" /><span style="color: red">*</span></label> <input
						type="text" class="form-control" id="inputLastName"
						name="lastName" required>
					<c:if test="${not empty errors and errors.containsKey('lastName')}">
						<p style="color: red">
							<c:out value="${errors.lastName}" />
						</p>
					</c:if>
				</div>

				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputLastName').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>

			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputEmail" class="form-label"><f:message
							key="email" /> <span style="color: red">*</span> </label> <input
						type="email" class="form-control" id="inputEmail" name="email">
					<c:if test="${not empty errors and errors.containsKey('email')}">
						<p style="color: red">
							<c:out value="${errors.email}" />
						</p>
					</c:if>
				</div>


				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputEmail').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

			</div>

			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputPhone" class="form-label"><f:message
							key="number" /><span style="color: red">*</span> </label> <input
						type="tel" class="form-control" id="inputPhone" name="phoneNumber"
						required>
					<c:if
						test="${not empty errors and errors.containsKey('phoneNumber')}">
						<p style="color: red">
							<c:out value="${errors.phoneNumber}" />
						</p>
					</c:if>
				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputPhone').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

			</div>

			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputPassword" class="form-label"><f:message
							key="password" /><span style="color: red">*</span></label> <input
						type="password" class="form-control" id="inputPassword"
						name="password" required>
					<c:if test="${not empty errors and errors.containsKey('password')}">
						<p style="color: red">
							<c:out value="${errors.password}" />
						</p>
					</c:if>
				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputPassword').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>
			<div class="row row-cols-auto">

				<div class="col-md-6">
					<label for="inputConfirm" class="form-label"><f:message
							key="repeatePassword" /><span style="color: red">*</span> </label> <input
						type="password" class="form-control" id="inputConfirm"
						name="confirmPassword" required>
					<c:if
						test="${not empty errors and errors.containsKey('confirmPassword')}">
						<p style="color: red">
							<c:out value="${errors.confirmPassword}" />
						</p>
					</c:if>
				</div>
				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputConfirm').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>


			<br>
			<button type="submit" class="btn btn-light">
				<f:message key="registered" />
			</button>
		</form>

	</main>
	<br>
	<c:import url="/WEB-INF/resources/footer.jspf" />


</body>
</html>