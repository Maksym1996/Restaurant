<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" uri = "WEB-INF/taglib.tld"%>
<!DOCTYPE html>
<html>
<head>
<META http-equiv="content-language" CONTENT="ru-RU">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Pizza Preferita</title>
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
<body class="d-flex flex-column h-100">
	<nav class="navbar navbar-expand-lg navbar-light bg-dark">
		<!-- Название -->
		<div class="container-fluid">
			<a class="navbar-brand"
				href="/restaurant-web/Pizza Preferita?page=${currentPage}">Pizza
				Preferita</a>

			<!-- Кнопка для выпадающего списка при уменьшении маштаба -->
			<button class="navbar-toggler collapsed" type="button"
				data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
				aria-controls="navbarSupportedContent" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="navbar-collapse collapse" id="navbarSupportedContent">

				<!-- Выпадающий список для языков-->
				<div class="dropdown">
					<button class="btn btn-secondary dropdown-toggle" type="button"
						id="dropdownMenuButton" data-toggle="dropdown"
						aria-expanded="false">
						<i class="bi bi-globe2"></i>
					</button>
					<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
						<li><a class="dropdown-item" href="#">English</a></li>
						<li><a class="dropdown-item" href="#">Русский</a></li>
					</ul>
				</div>

				<!--Иконки кабинета и корзины-->
				<div class="navbar-nav me-auto mb-2 mb-lg-0"></div>
				<div class="me-auto mb-2"></div>
				<div class="me-2">
					<a href="#" title="Личный кабинет"> <i
						class="bi bi-person-square cartMan"></i>
					</a>
				</div>
				<div class="me-2">
					<a href="#" title="Корзина"> <i class="bi bi-cart3 cartMan"></i>
					</a>
				</div>

			</div>
		</div>
	</nav>
	<!-- Кнопки фильтрации -->
		<form class = "form" action="Pizza Preferita" method="GET">
			<div class="form-check form-switch">
				<input class="form-check-input" type="checkbox"
					id="flexSwitchCheckDefault" value="Pizza" name="categories"> <label
					class="form-check-label filtres switchers" for="flexSwitchCheckDefault">Пицца <i class='fas fa-pizza-slice'></i> </label>
			</div>
			<div class="form-check form-switch">
				<input class="form-check-input" type="checkbox"
					id="flexSwitchCheckDefault" value="Burger" name="categories"> <label
					class="form-check-label filtres switchers" for="flexSwitchCheckDefault">Бургеры <i class='fas fa-hamburger'></i></label>
			</div>
			<div class="form-check form-switch">
				<input class="form-check-input" type="checkbox"
					id="flexSwitchCheckDefault" value="Drinks" name="categories"> <label
					class="form-check-label filtres switchers" for="flexSwitchCheckDefault">Напитки<i class="bi bi-cup-straw"></i></label>
			</div>
			<button type="submit" class="btn btn-outline-warning">Применить фильтр</button>
		</form>
	<main class="container">


		<!-- Вывод контента -->
		<div class="row ">

			<c:forEach var="product" items="${productsList}">
				<div class="col-sm-4">
					<div class="card cartBorder" style="width: 18rem;">
						<img src=<c:out value = "${product.imageLink}"/>
							class="card-img-top" alt="Pizza" height="250" width="250">
						<div class="card-body">
							<h5 class="card-title">
								<c:out value="${product.name}" />
							</h5>
							<p class="card-text">
								<c:out value="${product.description}" />
							</p>
							<div class="row row-cols-auto">
								<a href="#" class="btn btn-primary col-sm-7 cart">Заказать</a> <font
									size=+2 class="col-sm-5"><c:out value="${product.price}" />
									грн</font>
							</div>

						</div>
					</div>
				</div>
			</c:forEach>

		</div>

		<!-- Паджинация -->
		<div class="row row-cols-auto ">
			<div class="text-center col-sm-4"></div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage-1 > 0}">
					<a href='<t:page-link pageNumber="${currentPage - 1}" categories="${categories}"/>'
						class="padgination"> <i class="bi bi-arrow-left-circle-fill"></i>
					</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage - 2 > 0}">
					<a href='<t:page-link pageNumber="${currentPage - 2}" categories="${categories}"/>'
						class="padgination">${currentPage - 2}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage - 1 > 0}">
					<a href='<t:page-link pageNumber="${currentPage - 1}" categories="${categories}"/>'
						class="padgination">${currentPage - 1}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<a href='<t:page-link pageNumber="${currentPage}" categories="${categories}"/>'
					class="active">${currentPage}</a>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage + 1 <= maxPages}">
					<a href='<t:page-link pageNumber="${currentPage + 1}" categories="${categories}"/>' 
					class="padgination">${currentPage + 1}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage + 2 <= maxPages}">
					<a href='<t:page-link pageNumber="${currentPage + 2}" categories="${categories}"/>'
						class="padgination">${currentPage + 2}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage + 1 <= maxPages}">
					<a href='<t:page-link pageNumber="${currentPage + 1}" categories="${categories}"/>'
						class="padgination"><i class="bi bi-arrow-right-circle-fill"></i></a>
				</c:if>
			</div>
			<div class="text-center col-sm-2"></div>
		</div>
	</main>

	<!-- Вывод нижней строки с контантами -->
	<footer class="footer">
		<div class="row row-cols-auto">
			<div class="text-center col-sm-4">
				<i class="bi bi-phone"></i> <span>+380969055386</span>
			</div>
			<div class="text-center col-sm-4">
				<span>© Maksym 2021</span>
			</div>
			<div class="text-center col-sm-4">
				<i class="bi bi-envelope-fill"></i> <span>kordonets1996@ukr.net</span>
			</div>
		</div>
	</footer>
</body>
</html>