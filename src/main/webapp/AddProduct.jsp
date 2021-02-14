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

	<c:import url="/WEB-INF/resources/header.jsp" />

	<main>
		<!-- Registration form -->
		<form class="form" action="AddProduct" method="post"
			style="margin-left: 3rem">
			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputName" class="form-label">Название<span
						style="color: red">*</span></label> <input type="text"
						class="form-control" id="inputName" name="name" required>
					<c:if test="${not empty errors and errors.containsKey('name')}">
						<p style="color: red">
							<c:out value="${errors.name}" />
						</p>
					</c:if>

				</div>

				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputName').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>
			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputPrice" class="form-label">Цена<span
						style="color: red">*</span></label> <input type="number"
						class="form-control" id="inputPrice" name="price" required>
					<c:if test="${not empty errors and errors.containsKey('price')}">
						<p style="color: red">
							<c:out value="${errors.price}" />
						</p>
					</c:if>
					<c:if
						test="${not empty errors and errors.containsKey('pricePattern')}">
						<p style="color: red">
							<c:out value="${errors.pricePattern}" />
						</p>
					</c:if>
				</div>

				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputPrice').value = ''">
					<i class="far fa-times-circle"></i>
				</button>
			</div>

			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputDescription" class="form-label">Описание<span
						style="color: red">*</span>
					</label> <input type="text" class="form-control" id="inputDescription"
						name="description" required>
					<c:if
						test="${not empty errors and errors.containsKey('description')}">
						<p style="color: red">
							<c:out value="${errors.description}" />
						</p>
					</c:if>
				</div>


				<button type="button" style="color: red" class="btn btn-link "
					onclick="document.getElementById('inputDescription').value = ''">
					<i class="far fa-times-circle"></i>
				</button>

			</div>

			<div class="row row-cols-auto">
				<div class="col-md-6">
					<label for="inputImageLink" class="form-label">Ссылка на
						картинку<span style="color: red">*</span>
					</label> <input type="text" class="form-control" id="inputImageLink"
						name="imageLink" required>
					<c:if
						test="${not empty errors and errors.containsKey('imageLink')}">
						<p style="color: red">
							<c:out value="${errors.imageLink}" />
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
					<label for="inputCategory1" class="form-label">Категория:<span
						style="color: red">*</span></label>
					<p>
						<input type="radio" id="inputCategory1" name="category"
							value="Pizza" checked><label for="inputCategory1">Пицца</label>
					</p>
					<p>
						<input type="radio" id="inputCategory2" name="category"
							value="Burger"><label for="inputCategory2">Бургер</label>
					</p>
					<p>

						<input type="radio" id="inputCategory3" name="category"
							value="Drinks"><label for="inputCategory3">Напиток</label>
					</p>
					<c:if
						test="${not empty errors and errors.containsKey('category')}">
						<p style="color: red">
							<c:out value="${errors.category}" />
						</p>
					</c:if>
				</div>
				
			</div>
			<br>
			<button type="submit" class="btn btn-light">Добавить товар</button>
		</form>

	</main>
	<br>
	<c:import url="/WEB-INF/resources/footer.jspf" />


</body>
</html>