<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Act Test Summary</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <style>
        .accordion .card-header {
          cursor: pointer;
          border: none;
          background: none;
          font-weight: bold;
        }
        .accordion .card-body {
          border: none;
        }
        .successful {
          color: green;
        }
        .failed {
          color: red;
        }
        .skipped {
          color: grey;
        }
        .arrow {
          float: right;
          transition: transform 0.3s;
        }
        .card-body code {
          background-color: #f8f9fa;
          padding: 10px;
          display: block;
        }
        .card {
          margin-bottom: 10px;
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <h1>Act Test Summary</h1>
    <p class="lead">${execution.jobsCount} <#if execution.jobsCount == 1>job<#else>jobs</#if> run in ${execution.specsCount} <#if execution.specsCount == 1>spec<#else>specs</#if> with ${execution.failingJobsCount} <#if execution.failingJobsCount == 1>failure<#else>failures</#if></p>

    <div id="specsAccordion" class="accordion">
        <#list specs as spec>
            <div class="card">
                <div class="card-header ${spec.status}" id="heading${spec_index}" data-bs-toggle="collapse" data-bs-target="#collapse${spec_index}"
                     data-parent>
                    ${spec.name}
                    <span class="collapsed arrow">&#9660;</span>
                </div>
                <div id="collapse${spec_index}" class="collapse" aria-labelledby="heading${spec_index}}" data-parent="specsAccordion">
                    <div class="card-body">
                        <div id="jobAccordion${spec_index}">
                            <#list spec.jobs as job>
                                <div class="card">
                                    <div class="card-header ${job.status}" id="heading${spec_index}-${job_index}" data-bs-toggle="collapse"
                                         data-bs-target="#collapse${spec_index}-${job_index}">
                                        ${job.name}
                                        <span class="collapsed arrow">&#9660;</span>
                                    </div>
                                    <div id="collapse${spec_index}-${job_index}" class="collapse" aria-labelledby="heading${spec_index}-${job_index}">
                                        <div class="card-body">
                                            <code>${job.output}</code>
                                        </div>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>
            </div>
        </#list>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.slim.min.js"
        integrity="sha256-kmHvs0B+OpCW5GVHUNjv9rOmY0IvSIRcf7zGUDTDQM8=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script>
    $(document).ready(function(){
    $('.card-header').click(function(){
      var arrow = $(this).find('.arrow');
      if (arrow.hasClass("collapsed")) {
        arrow.removeClass("collapsed").html("&#9650");
      } else {
        arrow.addClass("collapsed").html("&#9660");
      }
    });
  });
</script>
</body>
</html>
