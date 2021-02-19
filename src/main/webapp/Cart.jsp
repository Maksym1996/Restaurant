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
<META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><f:message key="Cart.title" /></title>
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

	<div align="center" style="font-size: 1em">
		<f:message key="Cart.title" />
		<p>
			<f:message key="sumOrder" />
			:
			<c:out value="${orderSumm}" />
			<f:message key="grn" />
		</p>
		<form class="form" action="Cart" method="post"
			style="margin-left: 2rem">
			<button type="submit" class="btn btn-info">
				<f:message key="drawUpOrder" />
			</button>
			<input type="hidden" name="sum" value="${orderSumm}">
			<!-- User filling in the information required for sending -->
			<div class="row row-cols-auto" style="margin-top: 30px" align="left">

				<!-- The authorized user does not fill in the fields -->
				<c:if test="${empty user}">
					<div class="col-md-3">
						<label for="inputFirstName"><f:message key="firstName" /><span
							style="color: red">*</span></label> <input type="text"
							class="form-control" id="inputFirstName" name="firstName"
							required>
						<c:if
							test="${not empty errors and errors.containsKey('firstName')}">
							<p style="color: red">
								<c:out value="${errors.firstName}" />
							</p>
						</c:if>
					</div>
					<div class="col-md-3">
						<label for="inputPhoneNumber" class="formLabelCart"><f:message
								key="number" /><span style="color: red">*</span> </label> <input
							type="tel" class="form-control" id="inputPhoneNumber"
							name="phoneNumber" required>
						<c:if
							test="${not empty errors and errors.containsKey('phoneNumber')}">
							<p style="color: red">
								<c:out value="${errors.phoneNumber}" />
							</p>
						</c:if>
					</div>
				</c:if>
				<div class="col-md-4">
					<label for="inputAddress" class="formLabelCart"><f:message
							key="address" /><span style="color: red">*</span> </label> <input
						type="text" class="form-control" id="inputAddress" name="address"
						required>
					<c:if test="${not empty errors and errors.containsKey('address')}">
						<p style="color: red">
							<c:out value="${errors.address}" />
						</p>
					</c:if>
				</div>


			</div>
		</form>

	</div>

	<!-- Content output -->
	<main class="container">
		<div class="row ">
			<c:forEach var="product" items="${productsList}">
				<div class="col-md-2" style="color: black">
					<div class="card cartBorder" style="width: 12rem">
						<img src=<c:out value = "${product.imageLink}"/>
							class="card-img-top" alt="Pizza" height="170" width="150">
						<div class="card-body">
							<h5 class="card-title">
								<c:out value="${product.name}" />
							</h5>

							<div class="row row-cols-auto">

								<font size=+2 class="col"> <c:out
										value="${product.price}" /> <f:message key="grn" />
								</font>

							</div>

						</div>

						<!-- Buttons to increase and decrease the amount of products -->
						<div class="input-group">

							<a href='/restaurant-web/Cart?id=${product.id}&change=dec'
								class="input-group-btn">
								<button class="btn btn-outline-success" type="button">-</button>
							</a> <label
								class="form-control no-padding add-color text-center height-25">

								<c:forEach items="${count}" var="entry">
									<c:if test="${entry.key == product.id}">
    								${entry.value}
    								</c:if>
								</c:forEach>

							</label> <a href='/restaurant-web/Cart?id=${product.id}&change=inc'
								class="input-group-btn">
								<button class="btn btn-outline-success " type="button">+</button>
							</a>
						</div>


					</div>


				</div>

				<!-- Delete product from cart -->
				<form action="Cart" method="get" class="col-md-1">
					<button type="submit" style="color: red" class="btn btn-link "
						id="deleteId" name="deleteId"
						value=<c:out value="${product.id}" />>
						<i class="far fa-times-circle"></i>
					</button>
				</form>
			</c:forEach>

		</div>


	</main>

	<c:import url="/WEB-INF/resources/footer.jspf" />
</body>

</html>
