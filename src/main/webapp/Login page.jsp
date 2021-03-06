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
<title><f:message key="LoginPage.title" /></title>
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
		<div class="row row-cols-auto" style="margin-right: 0px">

			<span class="col-sm-4"></span>
			<div class="col-sm-4">
				<form action="Login page" method="post">
					<p class="formLabel">
						<f:message key="autorize" />
					</p>
					<!-- Email input -->
					<div class="form-floating">
						<input type="tel" class="form-control" id="inputTel"
							name="phoneNumber" required> <label for="inputTel"><f:message
								key="number" /></label>
						<c:if
							test="${not empty errors and errors.containsKey('phoneNumber')}">
							<p style="color: red">
								<c:out value="${errors.phoneNumber}" />
							</p>
						</c:if>
					</div>

					<!-- Password input -->
					<div class="form-floating">
						<input type="password" class="form-control" id="inputPassword"
							name="password" placeholder="" required> <label
							for="inputPassword"><f:message key="password" /></label>
						<c:if
							test="${not empty errors and errors.containsKey('password')}">
							<p style="color: red">
								<c:out value="${errors.password}" />
							</p>
						</c:if>
					</div>
					<div class="d-grid gap-2">
						<c:if
							test="${not empty errors and errors.containsKey('noUser')}">
							<p style="color: red">
								<c:out value="${errors.noUser}" />
							</p>
						</c:if>
					</div>
					<br>
					<!-- Submit button -->
					<div class="d-grid gap-2">
						<button type="submit" class="btn btn-light btn-block">
							<f:message key="logIn" />
						</button>
					</div>
				</form>
				<br>

				<!-- Form for registration -->
				<form class="d-grid gap-2 d-md-flex justify-content-md-end"
					action="Registration" method="get">
					<span class="formLabel" style="font-size: 1.5em"><f:message
							key="LoginPage.field" /></span>
					<button type="submit" class="btn btn-success btn-block">
						<f:message key="registered" />
					</button>
				</form>
			</div>
			<span class="col-sm-0"></span>
		</div>
	</main>
	<c:import url="/WEB-INF/resources/footer.jspf" />

</body>
</html>