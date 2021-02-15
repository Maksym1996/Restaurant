<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:if test="${not empty param.language}">
	<c:set var="language" value="${param.language}" scope="session" />
</c:if>
<f:setLocale value="${lang}" />
<f:setBundle basename="Bundles" />

<nav class="navbar navbar-expand-lg navbar-light bg-dark">
	<!-- Brand -->
	<div class="container-fluid">
		<a class="navbar-brand" href="/restaurant-web/Pizza Preferita?page=1">Pizza
			Preferita</a>

		<!-- Dropdown button when zooming out -->
		<button class="navbar-toggler collapsed" type="button"
			data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
			aria-controls="navbarSupportedContent" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>

		<div class="navbar-collapse collapse" id="navbarSupportedContent">

			<!-- Dropdown list for languages-->
			<div class="dropdown">
				<button class="btn btn-secondary dropdown-toggle" type="button"
					id="dropdownMenuButton" data-toggle="dropdown"
					aria-expanded="false">
					<i class="bi bi-globe2"></i>
				</button>
				<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
					<li><a class="dropdown-item" href="language?lang=en">English</a></li>
					<li><a class="dropdown-item" href="language?lang=ru">Русский</a></li>
				</ul>
			</div>

			<!--Icons for user account and shopping cart-->
			<div class="navbar-nav me-auto mb-2 mb-lg-0"></div>
			<div class="me-auto mb-2"></div>
			<div class="me-2">
				<c:if
					test="${not empty role and role != 'CLIENT' and role != 'ADMIN'}">
					<a href="WorkZone" title=<f:message key="header.workZone.title"/>> <i
						class="bi bi-briefcase-fill cartMan"></i>
					</a>
				</c:if>
			</div>

			<div class="me-2">
				<a href="Login page" title=<f:message key="header.loginPage.title"/>> <i
					class="bi bi-person-square cartMan"></i>
				</a>
			</div>
			<div class="me-2">
				<a href="Cart" title=<f:message key="header.cart.title"/>> <i
					class="bi bi-cart3 cartMan"></i>
				</a>
			</div>

		</div>
	</div>
</nav>