
/*
Permet de compléter le modal détail du livre
interroge le ws pour récupérer les détails du livre
 */

$(document).on("click", ".open-GetBookDialog", function () {
    var myBookId = $(this).data('id');

    $.ajax({
        type: 'GET',
        url : "/book/details/" + myBookId,
        data: '',
        contentType : 'text/plain',
        success: function(data){
            // formater la date
            var datePub = getFormattedDate(new Date(data.valueOf().dateOfficialRelease));
            var nbAvailable = data.valueOf().numberAvailable;
            // alimente dans le modal les balises html
            $("#bookId").val( myBookId );
            $("#bookTitle").html( data.valueOf().title );
            $("#bookNumberOfPage").html( data.valueOf().numberOfPage );
            $("#bookNumberAvailableLabel").html( "nombre de livre disponible(s)" );
            $("#bookNumberAvailable").html( nbAvailable );
            $("#bookNumberLentLabel").html("nombre d'ouvrage prêté");
            $("#bookNumberLent").html( data.valueOf().numberLent + "/" + data.valueOf().numberOfCopies);
            $("#bookNumberOnWaitingListLabel").html("nombre sur liste d'attente");
            $("#bookNumberOnWaitingList").html( data.valueOf().numberReserved + "/" + data.valueOf().numberAvailableForReservation);
            $("#bookFirstLoanDeadLineDateLabel").html( "disponible à partir du");
            $("#bookFirstLoanDeadLineDate").html( getFormattedDate(new Date(data.valueOf().firstLoanDeadLineDate )));
            $("#bookDateOfficialRelease").html( datePub );
            $("#bookSummary").html( data.valueOf().summary );
            if(nbAvailable === 0){
                // si plus de livre disponible on peut les réserver
                $("#bookNumberOnWaitingListLabel").show();
                $("#bookNumberOnWaitingList").show();
                $("#bookFirstLoanDeadLineDateLabel").show();
                $("#bookFirstLoanDeadLineDate").show();
                $("#bookNumberAvailableLabel").hide();
                $("#bookNumberAvailable").hide();
                $("#btnLoan").hide();
            }else{
                $("#bookNumberOnWaitingListLabel").hide();
                $("#bookNumberOnWaitingList").hide();
                $("#bookFirstLoanDeadLineDateLabel").hide();
                $("#bookFirstLoanDeadLineDate").hide();
                $("#bookNumberAvailableLabel").show();
                $("#bookNumberAvailable").show();
                // ajout de l'attribut onclick pour réserver le livre avec la fonction doAjaxPost() avec
                // l'id du livre comme attribut
                $("#btnLoan").show();
                $("#btnLoan").attr('onClick', "doAjaxPost('" + myBookId + "','loan')");
            }

            $('#getBookDetailsModal').modal('show');

        },
        error: function (data) {
            $("#getCode").html(data.responseText.fontcolor("red"));
            $("#getCodeModal").modal('show');


            setTimeout(function(){
                $('#getCodeModal').modal('hide')
            }, 2000);
        }
    });
});

// formater une date au format jj/mm/aaaa
function getFormattedDate(date) {
    var year = date.getFullYear();

    var month = (1 + date.getMonth()).toString();
    month = month.length > 1 ? month : '0' + month;

    var day = date.getDate().toString();
    day = day.length > 1 ? day : '0' + day;

    return day + '/' + month + '/' + year;
}

$(document).ready(function () {
    $('#clk_search').click(function () {
        var newURL = window.location.protocol + "//" + window.location.host;

        newURL = newURL
            + '/search/books?title=' + $('#TitleContains').val()
            + '&fullname=' + $('#fullnameAuthorContains').val()
            + '&category=' + $('#category option:selected').val();

        $("#BookBlock").load(newURL);

    });
});


function doExtend(id) {

    $.ajax({
        type: 'POST',
        url : "/loan/extend/" + id,
        data: '',
        success: function(response){
            $("#getCodeLoan").html(response.fontcolor("green"));
            $("#getCodeLoanModal").modal('show');

            setTimeout(function(){
                $('#getCodeLoanModal').modal('hide')
            }, 2000);

            // refresh des livres empruntés
            var newURL2 = window.location.protocol + "//" + window.location.host;

            newURL2 = newURL2
                + '/loan/findLoan/current/true';

            $("#LoanBlock").load(newURL2);
        },
        error: function (data) {
            $("#getCodeLoan").html(data.responseText.fontcolor("red"));
            $("#getCodeLoanModal").modal('show');


            setTimeout(function(){
                $('#getCodeLoanModal').modal('hide')
            }, 2000);
        }
    });

}

function doAjaxPost(id,type) {
    var newURL = window.location.protocol + "//" + window.location.host + '/'+ type +'/' + id;
    // type = 'loan';
    $.ajax({
        type: 'POST',
        url : "/"+ type +"/" + id,
        data: '',
        success: function(response){
            $("#getCode").html(response.fontcolor("green"));
            $("#getCodeModal").modal('show');
            // $("#LoanBlock").load(location.href + "#LoanBlock");
            setTimeout(function(){
                $('#getCodeModal').modal('hide')
            }, 2000);
            // refresh de la liste des résultats de la recherche (nb available -1)
            var newURL = window.location.protocol + "//" + window.location.host;

            newURL = newURL
                + '/search/books?title=' + $('#TitleContains').val()
                + '&fullname=' + $('#fullnameAuthorContains').val()
                + '&category=' + $('#category option:selected').val();

            $("#BookBlock").load(newURL);

            // refresh des livres empruntés
            var newURL2 = window.location.protocol + "//" + window.location.host;

            newURL2 = newURL2
                + '/loan/findLoan/current/true';


            $("#LoanBlock").load(newURL2);

            // refresh des livres reservés
            var newURL3 = window.location.protocol + "//" + window.location.host;

            newURL3 = newURL3
                + '/reservation';

            $("#ReservationBlock").load(newURL3);
        },
        error: function (data) {
            $("#getCode").html(data.responseText.fontcolor("red"));
            $("#getCodeModal").modal('show');


            setTimeout(function(){
                $('#getCodeModal').modal('hide')
            }, 2000);
        }
    });

}


function doAjaxDetailsBook(id) {
    var newURL = window.location.protocol + "//" + window.location.host;

    newURL = newURL
        + '/getDetailsBook/' + id;

    $.ajax({
        type: 'GET',
        url : "/book/details/" + id,
        data: '',
        contentType : 'text/plain',
        success: function(response){
            // refresh des livres empruntés
            // var objet = eval('(' + response + ')');
            // alert('rep ' + response);
            // console.log(objet);
            alert('tst');
            alert(response.getValue(1));
            $("#getBookDetailsModal").empty().append(response);

            $("#getBookDetailsModal").modal('show');

        },
        error: function (data) {
            $("#getCode").html(data.responseText.fontcolor("red"));
            $("#getCodeModal").modal('show');


            setTimeout(function(){
                $('#getCodeModal').modal('hide')
            }, 2000);
        }
    });

}


function doGetDetailsBook(book) {
    $("#getBookDetailsModal").empty().append(book);

    $("#getBookDetailsModal").modal('show');

}

function doCancelReservation(resaId) {
    $.ajax({
        type: 'POST',
        url : "/reservation/cancel/" + resaId,
        data: '',
        success: function(response){
            $("#getCodeReservation").html(response.fontcolor("green"));
            $("#getCodeReservationModal").modal('show');

            setTimeout(function(){
                $('#getCodeReservationModal').modal('hide')
            }, 2000);

            // refresh des livres réservés
            var newURL = window.location.protocol + "//" + window.location.host;

            // refresh des livres reservés
            var url = window.location.protocol + "//" + window.location.host + '/reservation';

            $("#ReservationBlock").load(url);

        },
        error: function (data) {
            $("#getCodeReservation").html(data.responseText.fontcolor("red"));
            $("#getCodeReservationModal").modal('show');


            setTimeout(function(){
                $('#getCodeReservationModal').modal('hide')
            }, 2000);
        }
    });

}