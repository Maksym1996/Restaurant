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
<title><f:message key="ManagePage.title" /></title>
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
		<div class="card w-50" style="margin-left: 3em">
			<div class="card-body">
				<h1 class="card-title">Найбольшее количество заказов:</h1>
				<div class="row productList">
					<div class="col-sm-3">
						<f:message key="firstName" />
					</div>
					<div class="col-sm-4">
						<f:message key="lastName" />
					</div>
					<div class="col-sm-3">
						<f:message key="number" />
					</div>
					<div class="col-sm-2">
						<f:message key="count" />
					</div>
				</div>
				<c:forEach var="userWithPerformedOrders"
					items="${usersWithPerformedOrders}">
					<div class="row productList">
						<div class="col-sm-3">
							<c:out value="${userWithPerformedOrders.firstName }" />
						</div>
						<div class="col-sm-4">
							<c:out value="${userWithPerformedOrders.lastName }" />
						</div>
						<div class="col-sm-3">
							<c:out value="${userWithPerformedOrders.phoneNumber }" />
						</div>
						<div class="col-sm-2">
							<c:out value="${userWithPerformedOrders.countOrders }" />
						</div>

					</div>
				</c:forEach>
			</div>
		</div>

		<h1 class="info">Текущие заказы</h1>
		<c:forEach var="receipt" items="${receiptsList}">

			<div class="card w-50" style="margin-left: 3em">
				<div class="card-body">
					<h5 class="card-title">
						№
						<c:out value="${receipt.order.id}" />
					</h5>
					<h4 class="card-title">


						<c:out value="${receipt.order.userFirstName}" />

						<c:out value="${receipt.order.userLastName}" />

						<f:message key="tel" />
						.
						<c:out value="${receipt.order.userPhoneNumber}" />



					</h4>
					<h3 class="card-title">
						<c:out value="${receipt.order.address}" />

					</h3>
					<h5 class="card-text">
						<c:out value="${receipt.order.orderDate}" />
					</h5>
					<h5>
						<span
							style="color: <c:out value="${receipt.order.status.getColor()}" /> ; font-weight: 900"><c:out
								value="${receipt.order.status}" /></span>
						<c:out value="${receipt.order.closingDate}" />

					</h5>
					<div class="row productList">
						<div class="col-sm-4">
							<f:message key="name" />
						</div>
						<div class="col-sm-2">
							<f:message key="count" />
						</div>
						<div class="col-sm-2">
							<f:message key="price" />
						</div>
					</div>
					<c:forEach var="content" items="${receipt.orderContent}">
						<c:if test="${orderView.id == order.id }">

							<div class="row productList">

								<div class="col-sm-4">
									<c:out value="${content.productName}" />
								</div>


								<div class="col-sm-2">
									<c:out value="${content.productCount }" />
								</div>
								<div class="col-sm-2">
									<c:out value="${content.productPrice * content.productCount}" />
								</div>
							</div>
						</c:if>
					</c:forEach>
					<h5 style="margin-top: 1em">
						<f:message key="sumOrder" />
						:
						<c:out value="${receipt.order.sum}" />
						<f:message key="grn" />
						.
					</h5>
				</div>
				<c:if
					test="${receipt.order.status.name() != 'REJECTED' and receipt.order.status.name() != 'PERFORMED'}">

					<div class="row" style="margin-top: 1em">
						<div class="col-sm-4">
							<form action="WorkZone" method="post">
								<input name="status" value="${receipt.order.status.name()}"
									type="hidden" /> <input name="id" value="${receipt.order.id}"
									type="hidden" />
								<button type="submit" class="btn btn-success">
									<f:message key="accept" />
								</button>
							</form>
						</div>
						<div class="col-sm-2">
							<form action="WorkZone" method="post">
								<input name="status" value="REJECTED" type="hidden" /> <input
									name="id" value="${receipt.order.id}" type="hidden" />
								<button type="submit" class="btn btn-danger">
									<f:message key="decline" />
								</button>
							</form>
						</div>
					</div>
				</c:if>
			</div>
		</c:forEach>
	</main>
	<c:import url="/WEB-INF/resources/footer.jspf" />

</body>
</html>