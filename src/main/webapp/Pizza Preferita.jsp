<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" uri="WEB-INF/taglib.tld"%>
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
<body class="d-flex flex-column h-100 mainPageBody">

	<c:import url="/WEB-INF/resources/header.jsp" />

	<!-- Filter buttons -->
	<div class="row row-cols-auto"
		style="margin-right: 0px; margin-left: 0px">
		<form class="form text-center col-sm-1.5" action="Pizza Preferita"
			method="GET">
			<div class="form-check form-switch">
				<input class="form-check-input" type="checkbox"
					id="flexSwitchCheckDefault1" value="Pizza" name="categories" /> <label
					class="form-check-label filtres switchers"
					for="flexSwitchCheckDefault1">Пицца <i
					class='fas fa-pizza-slice'></i>
				</label>

			</div>
			<div class="form-check form-switch">
				<input class="form-check-input" type="checkbox"
					id="flexSwitchCheckDefault2" value="Burger" name="categories">
				<label class="form-check-label filtres switchers"
					for="flexSwitchCheckDefault2">Бургеры <i
					class='fas fa-hamburger'></i></label>
			</div>
			<div class="form-check form-switch">
				<input class="form-check-input" type="checkbox"
					id="flexSwitchCheckDefault3" value="Drinks" name="categories" /> <label
					class="form-check-label filtres switchers"
					for="flexSwitchCheckDefault3">Напитки <i
					class="bi bi-cup-straw"></i></label>
			</div>
			<button type="submit" class="btn btn-outline-warning">Применить
				фильтр</button>
		</form>
		<c:if test="${not empty role and role == 'ADMIN' }">
			<div class="col-sm-8"></div>
			<form class="form text-center col-sm-2" action="AddProduct" method="get">
				<button type="submit" class="btn btn-success btn-lg">
					Добавить товар</i>
				</button>
			</form>
		</c:if>


	</div>
	<br>
	<!-- Sort menu -->
	<div class="dropdown sortMargin" style="margin-left: 20px">
		<button class="btn btn-warning dropdown-toggle" type="button"
			id="dropdownMenuButton" data-toggle="dropdown" aria-expanded="false">
			Отсортировать по</i>
		</button>
		<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
			<li><a class="dropdown-item"
				href='<t:page-link pageNumber="1" categories="${categories}" sortValue = "name" asc= "true"/>'>Названию</a></li>
			<li><a class="dropdown-item"
				href='<t:page-link pageNumber="1" categories="${categories}" sortValue = "price" asc= "true"/>'>От
					дешевых к дорогим</a></li>
			<li><a class="dropdown-item"
				href='<t:page-link pageNumber="1" categories="${categories}" sortValue = "price" asc= "false"/>'>От
					дорогих к дешевым</a></li>
			<li><a class="dropdown-item"
				href='<t:page-link pageNumber="1" categories="${categories}" sortValue = "category" asc= "true"/>'>Категориям</a></li>
		</ul>
	</div>
	<main class="container">


		<!-- Displaying content -->
		<div class="row ">

			<c:forEach var="product" items="${productsList}">
				<div class="col-md-4">
					<div class="card cartBorder" style="width: 18rem">
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

								<a
									href='<t:page-link pageNumber="${currentPage}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}" productId="${product.id}"/>'
									class="btn btn-primary col-sm-7 cart"> Заказать</a> <font
									size=+2 class="col-sm-5"> <c:out
										value="${product.price}" /> грн
								</font>

							</div>
							<c:if test="${not empty role and role == 'ADMIN' }">
								<div class="row row-cols-auto">
									<form class="form col-sm-5" action="UpdateProduct" method="get">
										<button type="submit" class="btn btn-primary btn-lg">
											Изменить</i>
										</button>
									</form>
									<form class="form col-sm-3" action="DeleteProduct" method="post">
									<input type="hidden" name="id" value="${product.id}">
										<button type="submit" class="btn btn-danger btn-lg">
											Удалить</i>
										</button>
									</form>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</c:forEach>

		</div>
		<div></div>
		<!-- Pagination -->
		<div class="row row-cols-auto">
			<div class="text-center col-sm-5"></div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage-1 > 0 && currentPage <= maxPages}">
					<a
						href='<t:page-link pageNumber="${currentPage - 1}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}"/>'
						class="padgination"> <i class="bi bi-arrow-left-circle-fill"></i>
					</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage - 2 > 0 && currentPage <= maxPages}">
					<a
						href='<t:page-link pageNumber="${currentPage - 2}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}"/>'
						class="padgination">${currentPage - 2}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage - 1 > 0 && currentPage <= maxPages}">
					<a
						href='<t:page-link pageNumber="${currentPage - 1}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}"/>'
						class="padgination">${currentPage - 1}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage > 0 && currentPage <= maxPages}">
					<a
						href='<t:page-link pageNumber="${currentPage}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}"/>'
						class="active">${currentPage}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage + 1 <= maxPages && currentPage > 0}">
					<a
						href='<t:page-link pageNumber="${currentPage + 1}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}"/>'
						class="padgination">${currentPage + 1}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage + 2 <= maxPages && currentPage > 0}">
					<a
						href='<t:page-link pageNumber="${currentPage + 2}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}"/>'
						class="padgination">${currentPage + 2}</a>
				</c:if>
			</div>
			<div class="text-center col-sm-0.5">
				<c:if test="${currentPage + 1 <= maxPages && currentPage > 0}">
					<a
						href='<t:page-link pageNumber="${currentPage + 1}" categories="${categories}" sortValue = "${sortValue}" asc= "${asc}"/>'
						class="padgination"><i class="bi bi-arrow-right-circle-fill"></i></a>
				</c:if>
			</div>
			<div class="text-center col-sm-2"></div>
		</div>
	</main>

	<c:import url="/WEB-INF/resources/footer.jspf" />

</body>
</html>