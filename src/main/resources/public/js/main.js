$(document).ready(function () {
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
    var $deleteBtn = $("#delete-event-btn");
    var $editBtn = $("#edit-event-btn");

    $filterMenu.change(function () {
        var $button = $("#filter-button");
        var selectedOptions = $(this).find("option:selected");
        if (selectedOptions.length > 0) {
            $button.prop('disabled', false);
        } else {
            $button.prop('disabled', true);
        }
    });

    $('#add-event-btn').click(function () {
        $currentlyOpened.hide();
        $(".reset-mode").click();
        $eventForm.prop('action', "/event/add");
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

    $categoryForm.submit(function () {
        event.preventDefault();
        var url = $(this).attr("action");
        $.post(url, {name: $('#cat-name-input').val()}).done(function (data) {
            var category = jQuery.parseJSON(data);
            var categorySelect = $("#categories");
            categorySelect.find("option:selected").removeAttr("selected");
            categorySelect.append("<option value='" + category.id + "' selected>" + category.name + "</option>");
            $("#cat-name-input").val("");
            $('#add-cat-btn').click();
            var alertMsg = "Category " + category.name + " successfully added!";
            var $alertDiv = generateAlert("success", alertMsg);
            $alertDiv.appendToAlertBox(alertFadeOutTimer);
        }, "json")
            .fail(function (jqXHR) {
                alert("ERROR!\n" + jQuery.parseJSON(jqXHR.responseText).message);
                return false;
            });
    });

    $eventForm.submit(function () {
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
            showAllEvents($alertDiv);
        }).fail(function (jqXHR) {
                var alertMsg = jQuery.parseJSON(jqXHR.responseText).message;
                var $alertDiv = generateAlert("danger", alertMsg);
                $currentlyOpened.fadeFromLoader($alertDiv, alertFadeOutTimer);
                return false;
            });
    });


    $('#filter-button').click(function () {
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
            showAllEvents($alertDiv);
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
        event.preventDefault();
    });

    $tableBody.on("click", ".view", function () {
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

    $tableBody.on("click", ".edit", function () {
        event.preventDefault();
        var route = "/event/" + this.id;
        $eventForm.prop('action', route);
        $eventForm.prop('method', "PUT");
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


    $tableBody.on("click", ".delete", function () {
        event.preventDefault();
        var route = "/event/" + this.id;
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
            });
        }
    });

    $("#filter-well").on("click", ".back-anchor", function () {
        showAllEvents();
    });

    $alertBox.on("click", ".reset-mode", function () {
        $(".mode-alert").remove();
        var events = $(".event-getter");
        events.removeClass($currentMode);
        events.addClass("view");
        $currentMode = "view";

    });

    $searchBox.on('keydown', function (e) {
        var $button = $("#search-button");
        if (e.which === 13 && $(this).val()) {
            $button.click();
            e.preventDefault();
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

    $("#search-button").click(function () {
        var searchPhrase = $searchBox.val();
        var route = "/event/find/" + searchPhrase;
        $searchBox.val('');
        $(this).prop('disabled', true);
        var msg = "";
        var $alertDiv = "";
        event.preventDefault();

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

    function showAllEvents(msg) {
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

    $.fn.appendToAlertBox = function (timeout) {
        $(this).appendTo($alertBox);
        if (timeout) {
            var alert = $(this);
            setTimeout(function () {
                alert.fadeOut(1000, function () {
                    $(this).remove();
                });
            }, timeout);
        }
    };

    function generateModeAlert(alertClass, msg) {
        $(".mode-alert").remove();
        html = "<div class='mode-alert alert alert-" + alertClass + " text-center'>" + msg + "</br><a href='#' class='reset-mode'>Take me back</a></div>";
        $(html).appendTo($alertBox);
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

    $('#name-input').val(event.name);
    $("#date-input").val(formDate);
    $("#desc-input").val(event.description);
    $('#event-form').find('option[value=' + event.description.id + ']').prop('selected', true)
}

function resetEventForm(){
    $('#name-input').val("");
    $("#date-input").val("");
    $("#desc-input").val("");
}

function generateAlert(alertClass, msg, additionalClass) {
    html = "<div class='alert alert-" + alertClass + " alert-dismissable fade in " + additionalClass +
        "'><a href='#' class='close' data-dismiss='alert' aria-label='close'>&times;</a>" +
        msg + "</div>";
    return $(html);
}

function generateRow(event) {
    return "<tr class='event-row'><td><a class='event-getter' href='#' id='" + event.id + "'>" + event.name + "</a></td>" +
        "<td>" + event.date + "</td>" +
        "<td>" + event.category.name + "</td></tr>"
}

function generateFilterWell(msg) {
    var html = "<p>" + msg + "</p> <a href='#' class='back-anchor'>Take me back</a>";
    $("#filter-well").html(html);
}

function populateEventTable(eventArray) {
    $(".event-row").remove();
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