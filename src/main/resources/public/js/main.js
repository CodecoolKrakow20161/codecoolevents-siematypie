$(document).ready(function () {
    var loggedIn;
    var sessionTimeout;
    var expirationDate;
    var alertFadeOutTimer = 3000;
    var $eventData = $("#event-data");
    var $table = $("#event-table");
    var $tableBody = $("#event-table-body");
    var $currentlyOpened = $table;
    var $currentMode = "view";
    var $loader = $("#loading");
    var $alertBox = $("#message-box");
    var $searchBox = $('#search-box');
    var $filterMenu = $('#filter-menu');
    var $adminBoard = $("#admin-board");
    var $categoryForm = $("#add-category");
    var $eventForm = $("#event-form");
    var $loginForm = $("#login-form");
    var $deleteBtn = $("#delete-event-btn");
    var $editBtn = $("#edit-event-btn");

    $.fn.appendToAlertBox = function (timeout) {
        $(this).appendTo($alertBox);
        if (timeout) {
            var alert = $(this);
            if (timeout){
                setTimeout(function () {
                    alert.fadeOut(1000, function () {
                        $(this).remove();
                    });
                }, timeout);
            }
        }
    };

    $("#jumbo-header").click(function (event) {
       showAllEvents(event);
    });

    function checkSession() {
        var tokenExpDate = Date.parse(localStorage.getItem('tokenExpirationDate'));
        var sessionTimeLeft = tokenExpDate - new Date();

        if (sessionTimeLeft > 1000 && loggedIn !== true){
            logIn();
            expirationDate = tokenExpDate;
            clearTimeout(sessionTimeout);
            sessionTimeout = setTimeout(logOutIfExpired, sessionTimeLeft);
        } else if (!localStorage.getItem('tokenExpirationDate') && loggedIn === true) {
            logOut("Your session has expired. Please log in again to get access to admin features.")
        }
    }

    checkSession();
    window.onfocus = function () {
        checkSession()
    };


    $filterMenu.change(function () {
        var $button = $("#filter-button");
        var selectedOptions = $(this).find("option:selected");
        if (selectedOptions.length > 0) {
            $button.prop('disabled', false);
        } else {
            $button.prop('disabled', true);
        }
    });

    $('#login-btn').click(function (event) {
        if (localStorage.getItem('x-admin-token')){
            localStorage.clear();
            event.preventDefault();
            event.stopPropagation();
            logOut();
        }

    });

    $('#add-event-btn').click(function () {
        $currentlyOpened.hide();
        $(".reset-mode").click();
        $("#action-name").text("Add new event");
        $eventForm.prop('action', "protected/event/add");
        $eventForm.prop('method', "POST");
        resetEventForm();
        $adminBoard.showAsCurrent();
    });

    $deleteBtn.click(function () {
        var modeName = "delete";
        var events = $(".event-getter");
        $currentlyOpened.hide();
        $table.showAsCurrent();
        events.removeClass($currentMode);
        $currentMode = modeName;
        events.addClass(modeName);
        generateModeAlert("danger", "You are now in delete mode. Click on event name to delete it.")
    });

    $editBtn.click(function () {
        var modeName = "edit";
        var events = $(".event-getter");
        $currentlyOpened.hide();
        $table.showAsCurrent();
        events.removeClass($currentMode);
        $currentMode = modeName;
        events.addClass(modeName);
        generateModeAlert("success", "You are now in edit mode. Click on event name to edit it.")
    });

    $('#add-cat-btn').click(function () {
        var body = $("body");
        setTimeout(function () {
            body.animate({scrollTop: $adminBoard.prop("scrollHeight")}, 500, 'swing');
            $("#cat-name-input").focus();
        }, 200);
    });

    $('#del-cat-btn').click(function () {
        var categorySelect = $("#categories");
        var selectedCat = categorySelect.find("option:selected");
        var selectpicker = $('.selectpicker');
        if(selectedCat.val() === "1"){
            alert("You can't delete general category!");
            return;
        }
        var r = confirm(
            "After deleting " + selectedCat.text() + " all events from it will be moved to 'general' category. Proceed? "
        );
        if (r === true){
            var route = "protected/category/" + selectedCat.val();
            $.ajax({
                url: route,
                type: 'DELETE',
                success: function (result) {
                    selectedCat.remove();
                    selectpicker.selectpicker();
                    selectpicker.find('option[value=' + selectedCat.val() + ']').remove();
                    selectpicker.selectpicker('refresh');
                    var $alertDiv = generateAlert("success", result);
                    $alertDiv.appendToAlertBox(alertFadeOutTimer);
                }
            }).fail(ajaxError);
        }

    });



    $categoryForm.submit(function (event) {
        event.preventDefault();
        var url = $(this).attr("action");
        $.post(url, {name: $('#cat-name-input').val()}).done(function (data) {
            var category = jQuery.parseJSON(data);
            var categorySelect = $("#categories");
            var selectpicker = $(".selectpicker");
            categorySelect.find("option:selected").removeAttr("selected");
            categorySelect.append("<option class='category-picker color-blk' value='" + category.id + "' selected>" + category.name + "</option>");

            selectpicker.selectpicker();
            selectpicker.append("<option class='filter-opt' value='" + category.id + "'>" + category.name + "</option>");
            selectpicker.selectpicker('refresh');

            $("#cat-name-input").val("");
            $('#add-cat-btn').click();
            var alertMsg = "Category " + category.name + " successfully added!";
            var $alertDiv = generateAlert("success", alertMsg);
            $alertDiv.appendToAlertBox(alertFadeOutTimer);
        }, "json")
            .fail(function (jqXHR) {
                alert("ERROR!\n" + jQuery.parseJSON(jqXHR.responseText).message);
                if(jqXHR.status === 401){
                    logOut()
                }
                return false;
            });
    });

    $eventForm.submit(function (event) {
        event.preventDefault();
        $currentlyOpened.hideToLoader();
        var url = $(this).attr("action");
        var method = $(this).attr("method");

        $.ajax({
            type: method,
            url: url,
            data: createFormJson(),
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            dataType: "text"
        }).done(function (data) {
            var $alertDiv = generateAlert("success", data);
            showAllEvents(event, $alertDiv);
        }).fail(ajaxError);
    });



    $loginForm.submit(function (event) {
        event.preventDefault();
        var url = $(this).attr("action");
        var method = $(this).attr("method");
        var data = {
            name: $("#login").val(), password:$('#password').val()
        };

        $.ajax({
            type: method,
            url: url,
            data: data,
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            dataType: "json"
        }).done(function (data, textStatus, request) {
            var token = request.getResponseHeader('x-admin-token');
            var delay = 1000;
            var expirationTime = data.expirationTime - delay;
            expirationDate = new Date();
            expirationDate.setMilliseconds(expirationDate.getMilliseconds() + expirationTime);
            localStorage.setItem('x-admin-token', token);
            localStorage.setItem('tokenExpirationDate', expirationDate);
            clearTimeout(sessionTimeout);
            sessionTimeout = setTimeout(logOutIfExpired, expirationTime);
            logIn();
            $('#login-close').click();
        }).fail(function (jqXHR) {
            var alertMsg = jQuery.parseJSON(jqXHR.responseText).message;
            alert(alertMsg);
            return false;
        });
    });

    function logOutIfExpired() {
        var tokenExpDate = Date.parse(localStorage.getItem('tokenExpirationDate'));
        if (tokenExpDate === expirationDate) {
            logOut("Your session has expired. Please log in again to get access to admin features.")
        } else if (localStorage.getItem('tokenExpirationDate')){
            expirationDate = tokenExpDate;
            sessionTimeout = setTimeout(logOutIfExpired, tokenExpDate - new Date());
        }
    }

    $('#filter-button').click(function (event) {
        event.preventDefault();
        var allCategories = $("option.filter-opt");
        var selectedOptions = $filterMenu.find("option:selected");
        var selectedValues = selectedOptions.map(function () {
            return $(this).val();
        }).get();
        var selectedNames = selectedOptions.map(function () {
            return this.text;
        }).get();

        $currentlyOpened.hideToLoader();
        var msg = "";
        if (allCategories.length === selectedOptions.length) {
            msg = "Showing events from all categories";
            var $alertDiv = generateAlert("success", msg, "search-result");
            showAllEvents(event, $alertDiv);
        } else {
            $.ajax({
                type: "POST",
                url: "/event/filter",
                data: JSON.stringify(selectedValues),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (data) {
                    if (data.length === 0) {
                        msg = "Sorry, but categories you have chosen currently have no events! :(";
                        $alertDiv = generateAlert("danger", msg, "search-result");
                        $currentlyOpened.fadeFromLoader($alertDiv, alertFadeOutTimer);
                    } else {
                        var wellMsg = "Showing events from following categories: " + selectedNames.join(", ");
                        populateEventTable(data);
                        $("#filter-well").html(generateFilterWell(wellMsg));
                        $table.fadeFromLoader();
                    }
                }
            });
        }
        $filterMenu.selectpicker('deselectAll');
    });

    $tableBody.on("click", ".view", function (event) {
        event.preventDefault();
        var route = "/event/" + this.id;
        $currentlyOpened.hideToLoader();

        $.getJSON(route, function (event) {
            populateEventData(event);
            $eventData.fadeFromLoader();

        })
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert("ERROR!\n" + jqXHR.responseJSON.message);
                location.reload();
            });
    });

    $tableBody.on("click", ".edit", function (event) {
        event.preventDefault();
        var route = "event/" + this.id;
        $eventForm.prop('action', "protected/" + route);
        $eventForm.prop('method', "PUT");
        $("#action-name").text("Edit event");
        $currentlyOpened.hideToLoader();

        $.getJSON(route, function (event) {
            populateEventForm(event);
            $adminBoard.fadeFromLoader();

        })
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert("ERROR!\n" + jqXHR.responseJSON.message);
                location.reload();
            });
    });


    $tableBody.on("click", ".delete", function (event) {
        event.preventDefault();
        var route = "protected/event/" + this.id;
        var r = confirm("Are you sure you want to delete event " + this.text);
        var currentNode = $(this);
        if (r === true) {
            $currentlyOpened.hideToLoader();
            $.ajax({
                url: route,
                type: 'DELETE',
                success: function (result) {
                    currentNode.closest(".event-row").remove();
                    var $alertDiv = generateAlert("success", result);
                    $table.fadeFromLoader($alertDiv, alertFadeOutTimer);
                }
            }).fail(ajaxError);
        }
    });

    $("#filter-well").on("click", ".back-anchor", function (event) {
        showAllEvents(event);
    });

    $alertBox.on("click", ".reset-mode", function (event) {
        event.preventDefault();
        resetMode();
    });

    function resetMode() {
        $(".mode-alert").remove();
        var events = $(".event-getter");
        events.removeClass($currentMode);
        events.addClass("view");
        $currentMode = "view";
    }

    $searchBox.on('keydown', function (event) {
        var $button = $("#search-button");
        if (event.which === 13 && $(this).val()) {
            $button.click();
            event.preventDefault();
            return false;

        }
        setTimeout(function () {   //calls click event after a certain time;
            if ($searchBox.val()) {
                $button.prop('disabled', false);
            } else {
                $button.prop('disabled', true);
            }
        }, 100);

    });

    $("#search-button").click(function (event) {
        event.preventDefault();
        var searchPhrase = $searchBox.val();
        var route = "/event/find/" + searchPhrase;
        $searchBox.val('');
        $(this).prop('disabled', true);
        var msg = "";
        var $alertDiv = "";

        $currentlyOpened.hideToLoader();
        $.getJSON(route, function (data) {
            $(".search-result").remove();
            if (data.length === 0) {
                msg = "Sorry, but nothing matches your search query! :(";
                $alertDiv = generateAlert("danger", msg, "search-result");
                $currentlyOpened.fadeFromLoader($alertDiv, alertFadeOutTimer);

            } else if (data.length === 1) {
                msg = "Found 1 match for your search query '" + searchPhrase + "'!";
                $alertDiv = generateAlert("success", msg, "search-result");
                populateEventData(data[0]);
                $eventData.fadeFromLoader($alertDiv, alertFadeOutTimer);
            } else {
                msg = "Found " + data.length + " matches for your search query '" + searchPhrase + "'!";
                var wellMsg = "Showing results for your seach query '" + searchPhrase + "'";
                $alertDiv = generateAlert("success", msg, "search-result");
                populateEventTable(data);
                $("#filter-well").html(generateFilterWell(wellMsg));
                $table.fadeFromLoader($alertDiv, alertFadeOutTimer);
            }
        })
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert("ERROR!\n" + jqXHR.responseJSON.message);
                location.reload();
            });
        return false;
    });

    function showAllEvents(event, msg) {
        event.preventDefault();
        var route = "/event/all";
        $currentlyOpened.hideToLoader();

        $.getJSON(route, function (data) {
            populateEventTable(data);
            $("#filter-well").html("");
            $table.fadeFromLoader(msg, alertFadeOutTimer);
        })
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert("ERROR!\n" + jqXHR.responseJSON.message);
                location.reload();
            });
    }

    $(".data-dismiss").click(function () {
        $(this).closest(".dismissable").hide();
        // showAllEvents();
        $table.showAsCurrent();
    });

    $.fn.showAsCurrent = function () {
        this.fadeIn();
        $currentlyOpened = $(this);
    };

    $.fn.hideToLoader = function (resetMode) {
        this.hide();
        $loader.show();
    };

    $.fn.fadeFromLoader = function (alertDiv, timeout) {
        var $currentNode = $(this);

        if ($currentNode.attr("id") === $eventData.attr("id") || $currentNode.attr("id") === $adminBoard.attr("id")) {
            $(".reset-mode").click();
        }
        $loader.fadeOut(300, function () {
            $currentNode.showAsCurrent();
            if (alertDiv) {
                alertDiv.appendToAlertBox(timeout)
            }
        });
    };



    function generateModeAlert(alertClass, msg) {
        $(".mode-alert").remove();
        html = "<div class='mode-alert alert alert-" + alertClass + " text-center'>" + msg + "</br><a href='#' class='reset-mode'>Take me back</a></div>";
        $(html).appendTo($alertBox);
    }

    function logIn() {
        $(".login-alert").remove();
        $.ajaxSetup({
            headers: { 'x-admin-token': localStorage.getItem('x-admin-token') }
        });
        generateAlert("success", "Hello, Admin!", "login-alert").appendToAlertBox(alertFadeOutTimer);
        $('#login-btn').html("<span class='glyphicon glyphicon-log-out'></span> Log out");
        $('#admin-btns').fadeIn();
        loggedIn = true;
    }

    function logOut(msg) {
        if (loggedIn){
            $(".login-alert").remove();
            loggedIn = false;
            localStorage.clear();
            $('#login-btn').html("<span class='glyphicon glyphicon-user'></span> Login as administrator");
            $('#admin-btns').hide();
            $currentlyOpened.hide();
            $loader.hide();
            $table.showAsCurrent();
            if (msg){
                var alert = generateAlert("danger", msg, "login-alert");
                alert.appendToAlertBox();
            }
            resetMode();
        }
    }

    function generateAlert(alertClass, msg, additionalClass) {
        html = "<div class='alert alert-" + alertClass + " alert-dismissable fade in " + additionalClass +
            "'><a href='#' class='close' data-dismiss='alert' aria-label='close'>&times;</a>" +
            msg + "</div>";
        return $(html);
    }

    function ajaxError(jqXHR) {
        var alertMsg = jQuery.parseJSON(jqXHR.responseText).message;
        if (jqXHR.status === 401){
            logOut(alertMsg);
            return false;
        }
        var $alertDiv = generateAlert("danger", alertMsg);
        $currentlyOpened.fadeFromLoader($alertDiv, alertFadeOutTimer);
        return false;
    }

});

function populateEventData(event) {
    $("#chosen-event-name").text(event.name);
    $("#chosen-event-date").text("Date: " + event.date);
    $("#chosen-event-cat").text("Category: " + event.category.name);
    $("#chosen-event-desc").html("<p>" + event.description + "</p>");
}

function populateEventForm(event) {
    var dateArray = event.date.split("-");
    var formDate = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
    var categorySelect = $("#categories");
    categorySelect.find("option:selected").removeAttr("selected");

    $('#name-input').val(event.name);
    $("#date-input").val(formDate);
    $("#desc-input").val(event.description);
    var toSelect = categorySelect.find('option[value=' + event.category.id + ']');
    toSelect.prop('selected',true);

}

function resetEventForm(){
    $('#name-input').val("");
    $("#date-input").val("");
    $("#desc-input").val("");
}

function generateRow(event) {
    return "<tr class='event-row'><td><a class='event-getter view' href='#' id='" + event.id + "'>" + event.name + "</a></td>" +
        "<td>" + event.date + "</td>" +
        "<td>" + event.category.name + "</td></tr>"
}

function generateFilterWell(msg) {
    var html = "<p>" + msg + "</p> <a href='#' class='back-anchor'>Take me back</a>";
    $("#filter-well").html(html);
}

function populateEventTable(eventArray) {
    $(".event-row").remove();
    if (eventArray.length !== 0){
       $("#no-events").remove();
    }
    var $tableBody = $("#event-table-body");
    $.each(eventArray, function (index, event) {
        var row = generateRow(event);
        $tableBody.append(row);
    });
}

function createFormJson() {
    return {
        name: $('#name-input').val(),
        date: $("#date-input").val(),
        desc: $("#desc-input").val(),
        catId: $("#categories").find("option:selected").val()
    }

}